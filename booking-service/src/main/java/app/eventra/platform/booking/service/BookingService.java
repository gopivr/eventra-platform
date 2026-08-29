package app.eventra.platform.booking.service;

import app.eventra.platform.booking.domain.AdmissionCredential;
import app.eventra.platform.booking.domain.BookingOutboxEvent;
import app.eventra.platform.booking.domain.CheckIn;
import app.eventra.platform.booking.domain.SeatHold;
import app.eventra.platform.booking.domain.SeatHoldItem;
import app.eventra.platform.booking.domain.Ticket;
import app.eventra.platform.booking.domain.TicketOrder;
import app.eventra.platform.booking.repository.AdmissionCredentialRepository;
import app.eventra.platform.booking.repository.BookingOutboxEventRepository;
import app.eventra.platform.booking.repository.CheckInRepository;
import app.eventra.platform.booking.repository.SeatHoldItemRepository;
import app.eventra.platform.booking.repository.SeatHoldRepository;
import app.eventra.platform.booking.repository.TicketOrderRepository;
import app.eventra.platform.booking.repository.TicketRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {
  private static final Duration HOLD_DURATION = Duration.ofMinutes(10);
  private final SeatHoldRepository holds;
  private final SeatHoldItemRepository holdItems;
  private final TicketOrderRepository orders;
  private final TicketRepository tickets;
  private final CheckInRepository checkIns;
  private final BookingOutboxEventRepository outbox;
  private final AdmissionCredentialRepository credentials;
  private final StringRedisTemplate redis;
  private final SecureRandom random = new SecureRandom();

  public BookingService(
      SeatHoldRepository holds,
      SeatHoldItemRepository holdItems,
      TicketOrderRepository orders,
      TicketRepository tickets,
      CheckInRepository checkIns,
      BookingOutboxEventRepository outbox,
      AdmissionCredentialRepository credentials,
      StringRedisTemplate redis) {
    this.holds = holds;
    this.holdItems = holdItems;
    this.orders = orders;
    this.tickets = tickets;
    this.checkIns = checkIns;
    this.outbox = outbox;
    this.credentials = credentials;
    this.redis = redis;
  }

  @Transactional
  public SeatHold hold(UUID user, UUID event, List<UUID> seats) {
    if (seats == null || seats.isEmpty() || seats.stream().distinct().count() != seats.size())
      throw new IllegalArgumentException("seat list must be non-empty and unique");
    UUID holdId = UUID.randomUUID();
    List<String> acquired = new java.util.ArrayList<>();
    try {
      for (UUID seat : seats) {
        String redisKey = seatKey(seat);
        if (!Boolean.TRUE.equals(
            redis.opsForValue().setIfAbsent(redisKey, holdId.toString(), HOLD_DURATION)))
          throw new IllegalStateException("seat unavailable");
        acquired.add(redisKey);
      }
      SeatHold hold =
          holds.save(new SeatHold(holdId, user, event, Instant.now().plus(HOLD_DURATION)));
      seats.forEach(seat -> holdItems.save(new SeatHoldItem(holdId, seat)));
      return hold;
    } catch (RuntimeException failure) {
      acquired.forEach(redisKey -> releaseIfOwned(redisKey, holdId));
      throw failure;
    }
  }

  @Transactional
  public void release(UUID holdId) {
    SeatHold hold = holds.findById(holdId).orElseThrow();
    hold.release();
    holds.save(hold);
    releaseRedisSeats(holdId);
  }

  @Transactional
  public TicketOrder order(
      UUID user,
      UUID event,
      String idempotencyKey,
      String currency,
      long subtotal,
      long tax,
      long fee) {
    return orders
        .findByUserIdAndIdempotencyKey(user, idempotencyKey)
        .orElseGet(
            () ->
                orders.save(
                    new TicketOrder(
                        UUID.randomUUID(),
                        user,
                        event,
                        idempotencyKey,
                        currency,
                        subtotal,
                        tax,
                        fee,
                        "{\"currency\":\""
                            + currency
                            + "\",\"totalMinor\":"
                            + (subtotal + tax + fee)
                            + "}")));
  }

  @Transactional
  public List<Ticket> issue(UUID orderId) {
    TicketOrder order = orders.findById(orderId).orElseThrow();
    if (!"CONFIRMED".equals(order.getStatus()))
      throw new IllegalStateException("order payment is not confirmed");
    List<Ticket> existing = tickets.findByOrderId(orderId);
    if (!existing.isEmpty()) return existing;
    Ticket ticket = tickets.save(new Ticket(UUID.randomUUID(), orderId, null));
    rotateCredential(ticket.getId());
    UUID messageId = UUID.randomUUID();
    outbox.save(
        new BookingOutboxEvent(
            messageId,
            orderId.toString(),
            "{\"messageId\":\""
                + messageId
                + "\",\"orderId\":\""
                + orderId
                + "\",\"ticketId\":\""
                + ticket.getId()
                + "\",\"userId\":\""
                + order.getUserId()
                + "\",\"eventId\":\""
                + order.getEventId()
                + "\",\"occurredAt\":\""
                + Instant.now()
                + "\"}"));
    return List.of(ticket);
  }

  @Transactional
  public List<Ticket> confirmAndIssue(UUID orderId) {
    TicketOrder order = orders.findById(orderId).orElseThrow();
    if ("PENDING_PAYMENT".equals(order.getStatus())) {
      order.confirm();
      orders.save(order);
    }
    return issue(orderId);
  }

  public Optional<TicketOrder> findOrder(UUID id) {
    return orders.findById(id);
  }

  public List<Ticket> findTickets(UUID orderId, UUID requestingUser) {
    requireOrderOwner(orderId, requestingUser);
    return tickets.findByOrderId(orderId);
  }

  @Transactional
  public Ticket assign(
      UUID ticketId, UUID requestingUser, UUID assigneeUserId, String assigneeName) {
    Ticket ticket = tickets.findById(ticketId).orElseThrow();
    requireOrderOwner(ticket.getOrderId(), requestingUser);
    ticket.assign(assigneeUserId, assigneeName);
    return tickets.save(ticket);
  }

  @Transactional
  public String rotateCredential(UUID ticketId, UUID requestingUser) {
    Ticket ticket = tickets.findById(ticketId).orElseThrow();
    requireOrderOwner(ticket.getOrderId(), requestingUser);
    return rotateCredential(ticketId);
  }

  @Transactional
  public CheckIn checkIn(UUID eventId, String rawCredential, String scanner) {
    AdmissionCredential credential =
        credentials
            .findByTokenHash(hash(rawCredential))
            .orElseThrow(() -> new IllegalArgumentException("invalid admission credential"));
    Ticket ticket = tickets.findById(credential.getTicketId()).orElseThrow();
    TicketOrder order = orders.findById(ticket.getOrderId()).orElseThrow();
    if (!order.getEventId().equals(eventId))
      throw new IllegalArgumentException("credential is not valid for this event");
    Optional<CheckIn> existing = checkIns.findByTicketId(ticket.getId());
    if (existing.isPresent()) return existing.get();
    ticket.checkIn();
    tickets.save(ticket);
    try {
      return checkIns.save(new CheckIn(UUID.randomUUID(), ticket.getId(), eventId, scanner));
    } catch (DataIntegrityViolationException race) {
      return checkIns.findByTicketId(ticket.getId()).orElseThrow(() -> race);
    }
  }

  @Transactional
  public void reconcileHolds(Instant now) {
    holds
        .findByStatusAndExpiresAtLessThanEqual("ACTIVE", now)
        .forEach(
            hold -> {
              hold.expire();
              holds.save(hold);
              releaseRedisSeats(hold.getId());
            });
    holds
        .findByStatusAndExpiresAtAfter("ACTIVE", now)
        .forEach(
            hold -> {
              Duration remaining = Duration.between(now, hold.getExpiresAt());
              holdItems
                  .findByHoldId(hold.getId())
                  .forEach(
                      item ->
                          redis
                              .opsForValue()
                              .setIfAbsent(
                                  seatKey(item.getSeatId()), hold.getId().toString(), remaining));
            });
  }

  private String rotateCredential(UUID ticketId) {
    String raw = randomToken();
    credentials.save(new AdmissionCredential(ticketId, hash(raw)));
    return raw;
  }

  private void requireOrderOwner(UUID orderId, UUID userId) {
    TicketOrder order = orders.findById(orderId).orElseThrow();
    if (!order.getUserId().equals(userId))
      throw new org.springframework.security.access.AccessDeniedException(
          "ticket order is owned by another user");
  }

  private void releaseRedisSeats(UUID holdId) {
    holdItems
        .findByHoldId(holdId)
        .forEach(item -> releaseIfOwned(seatKey(item.getSeatId()), holdId));
  }

  private void releaseIfOwned(String redisKey, UUID holdId) {
    if (holdId.toString().equals(redis.opsForValue().get(redisKey))) redis.delete(redisKey);
  }

  private String seatKey(UUID seat) {
    return "eventra:seat:" + seat;
  }

  private String randomToken() {
    byte[] bytes = new byte[32];
    random.nextBytes(bytes);
    return HexFormat.of().formatHex(bytes);
  }

  private String hash(String token) {
    if (token == null || token.isBlank())
      throw new IllegalArgumentException("admission credential is required");
    try {
      return HexFormat.of()
          .formatHex(
              MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException impossible) {
      throw new IllegalStateException(impossible);
    }
  }
}

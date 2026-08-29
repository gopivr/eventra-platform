package app.eventra.platform.booking.api;

import app.eventra.platform.booking.domain.SeatHold;
import app.eventra.platform.booking.domain.Ticket;
import app.eventra.platform.booking.domain.TicketOrder;
import app.eventra.platform.booking.service.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class BookingController {
  private final BookingService service;

  public BookingController(BookingService service) {
    this.service = service;
  }

  public record HoldRequest(@NotNull UUID eventId, @NotEmpty List<UUID> seatIds) {}

  public record HoldResponse(
      UUID holdId, UUID eventId, String status, java.time.Instant expiresAt) {}

  public record OrderRequest(
      @NotNull UUID eventId,
      @NotBlank String currency,
      @PositiveOrZero long subtotalMinor,
      @PositiveOrZero long taxMinor,
      @PositiveOrZero long feeMinor) {}

  public record OrderResponse(UUID id, String status, long totalMinor) {}

  public record TicketResponse(UUID id, String status, UUID assigneeUserId, String assigneeName) {}

  public record AssignmentRequest(UUID assigneeUserId, String assigneeName) {}

  public record CredentialResponse(String credential) {}

  public record CheckInRequest(@NotBlank String credential) {}

  @PostMapping("/seat-holds")
  public ResponseEntity<HoldResponse> hold(
      @Valid @RequestBody HoldRequest request, Authentication authentication) {
    SeatHold hold = service.hold(user(authentication), request.eventId(), request.seatIds());
    return ResponseEntity.created(URI.create("/v1/seat-holds/" + hold.getId()))
        .body(
            new HoldResponse(
                hold.getId(), hold.getEventId(), hold.getStatus(), hold.getExpiresAt()));
  }

  @DeleteMapping("/seat-holds/{id}")
  public ResponseEntity<Void> release(@PathVariable UUID id) {
    service.release(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/ticket-orders")
  public ResponseEntity<OrderResponse> order(
      @RequestHeader("Idempotency-Key") String key,
      @Valid @RequestBody OrderRequest request,
      Authentication authentication) {
    TicketOrder order =
        service.order(
            user(authentication),
            request.eventId(),
            key,
            request.currency(),
            request.subtotalMinor(),
            request.taxMinor(),
            request.feeMinor());
    return ResponseEntity.created(URI.create("/v1/ticket-orders/" + order.getId()))
        .body(new OrderResponse(order.getId(), order.getStatus(), order.getTotalMinor()));
  }

  @GetMapping("/ticket-orders/{id}")
  public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
    return service
        .findOrder(id)
        .map(
            order ->
                ResponseEntity.ok(
                    new OrderResponse(order.getId(), order.getStatus(), order.getTotalMinor())))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/ticket-orders/{id}/tickets")
  public List<TicketResponse> tickets(@PathVariable UUID id, Authentication authentication) {
    return service.findTickets(id, user(authentication)).stream().map(this::response).toList();
  }

  @PutMapping("/tickets/{id}/assignment")
  public TicketResponse assign(
      @PathVariable UUID id,
      @RequestBody AssignmentRequest request,
      Authentication authentication) {
    return response(
        service.assign(id, user(authentication), request.assigneeUserId(), request.assigneeName()));
  }

  @PostMapping("/tickets/{id}/credential-rotations")
  public CredentialResponse rotateCredential(@PathVariable UUID id, Authentication authentication) {
    return new CredentialResponse(service.rotateCredential(id, user(authentication)));
  }

  @PostMapping("/events/{eventId}/check-ins")
  public ResponseEntity<UUID> checkIn(
      @PathVariable UUID eventId,
      @Valid @RequestBody CheckInRequest request,
      Authentication authentication) {
    return ResponseEntity.ok(
        service.checkIn(eventId, request.credential(), authentication.getName()).getId());
  }

  @PostMapping("/internal/ticket-orders/{id}/issuance")
  public List<UUID> issue(@PathVariable UUID id) {
    return service.issue(id).stream().map(Ticket::getId).toList();
  }

  private UUID user(Authentication authentication) {
    return UUID.nameUUIDFromBytes(authentication.getName().getBytes(StandardCharsets.UTF_8));
  }

  private TicketResponse response(Ticket ticket) {
    return new TicketResponse(
        ticket.getId(), ticket.getStatus(), ticket.getAssigneeUserId(), ticket.getAssigneeName());
  }
}

package app.eventra.platform.booking.api;

import app.eventra.platform.booking.domain.Cancellation;
import app.eventra.platform.booking.domain.Invitation;
import app.eventra.platform.booking.domain.ProviderAvailability;
import app.eventra.platform.booking.domain.Quote;
import app.eventra.platform.booking.domain.ServiceBooking;
import app.eventra.platform.booking.domain.ServiceRequest;
import app.eventra.platform.booking.repository.CancellationRepository;
import app.eventra.platform.booking.repository.InvitationRepository;
import app.eventra.platform.booking.repository.ProviderAvailabilityRepository;
import app.eventra.platform.booking.repository.QuoteRepository;
import app.eventra.platform.booking.repository.ServiceBookingRepository;
import app.eventra.platform.booking.repository.ServiceRequestRepository;
import app.eventra.platform.booking.service.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class BookingWorkflowController {
  private final InvitationRepository invitations;
  private final ServiceRequestRepository requests;
  private final QuoteRepository quotes;
  private final ServiceBookingRepository bookings;
  private final ProviderAvailabilityRepository availability;
  private final CancellationRepository cancellations;
  private final BookingService booking;

  public BookingWorkflowController(
      InvitationRepository invitations,
      ServiceRequestRepository requests,
      QuoteRepository quotes,
      ServiceBookingRepository bookings,
      ProviderAvailabilityRepository availability,
      CancellationRepository cancellations,
      BookingService booking) {
    this.invitations = invitations;
    this.requests = requests;
    this.quotes = quotes;
    this.bookings = bookings;
    this.availability = availability;
    this.cancellations = cancellations;
    this.booking = booking;
  }

  private UUID user(Authentication a) {
    return UUID.nameUUIDFromBytes(a.getName().getBytes());
  }

  public record InvitationRequest(@NotNull UUID eventId, @Email @NotBlank String inviteeEmail) {}

  public record Response(UUID id) {}

  public record ServiceRequestBody(@NotNull UUID eventId, @NotBlank String details) {}

  public record QuoteBody(
      @NotNull UUID providerId,
      @PositiveOrZero long amountMinor,
      @Pattern(regexp = "[A-Z]{3}") String currency) {}

  public record AvailabilityBody(@NotNull Instant startsAt, @NotNull Instant endsAt) {}

  public record CancellationBody(String reason) {}

  @PostMapping("/invitations")
  public ResponseEntity<Response> invite(
      @Valid @RequestBody InvitationRequest r, Authentication a) {
    Invitation i =
        invitations.save(
            new Invitation(
                UUID.randomUUID(),
                r.eventId(),
                user(a),
                r.inviteeEmail(),
                hash(UUID.randomUUID() + a.getName()),
                Instant.now().plus(Duration.ofDays(7))));
    return ResponseEntity.created(URI.create("/v1/invitations/" + i.getId()))
        .body(new Response(i.getId()));
  }

  @PostMapping("/invitations/{id}/responses")
  public ResponseEntity<Void> respond(@PathVariable UUID id, @RequestParam String response) {
    invitations.findById(id).orElseThrow().respond(response);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/service-requests")
  public ResponseEntity<Response> request(
      @Valid @RequestBody ServiceRequestBody r, Authentication a) {
    ServiceRequest s =
        requests.save(
            new ServiceRequest(
                UUID.randomUUID(),
                r.eventId(),
                user(a),
                r.details(),
                Instant.now().plus(Duration.ofDays(2))));
    return ResponseEntity.created(URI.create("/v1/service-requests/" + s.getId()))
        .body(new Response(s.getId()));
  }

  @PostMapping("/service-requests/{id}/quotes")
  public ResponseEntity<Response> quote(@PathVariable UUID id, @Valid @RequestBody QuoteBody r) {
    ServiceRequest s = requests.findById(id).orElseThrow();
    s.quote();
    requests.save(s);
    Quote q =
        quotes.save(
            new Quote(
                UUID.randomUUID(),
                id,
                r.providerId(),
                r.amountMinor(),
                r.currency(),
                Instant.now().plus(Duration.ofDays(1))));
    return ResponseEntity.created(URI.create("/v1/quotes/" + q.getId()))
        .body(new Response(q.getId()));
  }

  @PostMapping("/service-requests/{id}/quote-selection")
  public ResponseEntity<Response> select(@PathVariable UUID id, @RequestParam UUID quoteId) {
    if (!requests.existsById(id) || !quotes.existsById(quoteId))
      return ResponseEntity.notFound().build();
    ServiceBooking b = bookings.save(new ServiceBooking(UUID.randomUUID(), id, quoteId));
    return ResponseEntity.created(URI.create("/v1/service-bookings/" + b.getId()))
        .body(new Response(b.getId()));
  }

  @PostMapping("/service-bookings/{id}/acceptance")
  public ResponseEntity<Void> accept(@PathVariable UUID id) {
    return bookings
        .findById(id)
        .map(
            b -> {
              b.accept();
              bookings.save(b);
              return ResponseEntity.noContent().<Void>build();
            })
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping("/service-bookings/{id}/decline")
  public ResponseEntity<Void> decline(@PathVariable UUID id) {
    return bookings
        .findById(id)
        .map(
            b -> {
              b.decline();
              bookings.save(b);
              return ResponseEntity.noContent().<Void>build();
            })
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping("/service-bookings/{id}/cancellations")
  public ResponseEntity<Response> cancelBooking(
      @PathVariable UUID id, @RequestBody(required = false) CancellationBody r, Authentication a) {
    if (!bookings.existsById(id)) return ResponseEntity.notFound().build();
    Cancellation c =
        cancellations.save(
            new Cancellation(UUID.randomUUID(), null, id, user(a), r == null ? null : r.reason()));
    return ResponseEntity.accepted().body(new Response(c.getId()));
  }

  @PostMapping("/ticket-orders/{id}/cancellations")
  public ResponseEntity<Response> cancelOrder(
      @PathVariable UUID id, @RequestBody(required = false) CancellationBody r, Authentication a) {
    if (booking.findOrder(id).isEmpty()) return ResponseEntity.notFound().build();
    Cancellation c =
        cancellations.save(
            new Cancellation(UUID.randomUUID(), id, null, user(a), r == null ? null : r.reason()));
    return ResponseEntity.accepted().body(new Response(c.getId()));
  }

  @GetMapping("/providers/me/availability")
  public List<ProviderAvailability> getAvailability(Authentication a) {
    return availability.findByProviderId(user(a));
  }

  @PostMapping("/providers/me/availability-blocks")
  public ResponseEntity<Response> addAvailability(
      @Valid @RequestBody AvailabilityBody r, Authentication a) {
    ProviderAvailability v =
        availability.save(
            new ProviderAvailability(UUID.randomUUID(), user(a), r.startsAt(), r.endsAt()));
    return ResponseEntity.created(URI.create("/v1/providers/me/availability/" + v.getId()))
        .body(new Response(v.getId()));
  }

  private String hash(String value) {
    try {
      return HexFormat.of()
          .formatHex(
              java.security.MessageDigest.getInstance("SHA-256")
                  .digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}

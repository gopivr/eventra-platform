package app.eventra.platform.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cancellation", schema = "eventra_booking")
public class Cancellation {
  @Id private UUID id;

  @Column(name = "order_id")
  private UUID orderId;

  @Column(name = "service_booking_id")
  private UUID serviceBookingId;

  @Column(name = "requested_by", nullable = false)
  private UUID requestedBy;

  private String reason;

  @Column(nullable = false)
  private String status = "REQUESTED";

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected Cancellation() {}

  public Cancellation(UUID id, UUID order, UUID booking, UUID requester, String reason) {
    if ((order == null) == (booking == null))
      throw new IllegalArgumentException("exactly one cancellation target is required");
    this.id = id;
    orderId = order;
    serviceBookingId = booking;
    requestedBy = requester;
    this.reason = reason;
    createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }
}

package app.eventra.platform.engagement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "dispute", schema = "eventra_engagement")
public class Dispute {
  @Id private UUID id;

  @Column(name = "opened_by_user_id", nullable = false)
  private UUID openedByUserId;

  @Column(name = "booking_id", nullable = false)
  private String bookingId;

  @Column(name = "payment_id", nullable = false)
  private String paymentId;

  @Column(nullable = false)
  private String status = "OPEN";

  @Column(nullable = false)
  private String reason;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected Dispute() {}

  public Dispute(UUID id, UUID user, String booking, String payment, String reason) {
    this.id = id;
    openedByUserId = user;
    bookingId = booking;
    paymentId = payment;
    this.reason = reason;
    createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public UUID getOpenedByUserId() {
    return openedByUserId;
  }

  public String getStatus() {
    return status;
  }

  public String getReason() {
    return reason;
  }
}

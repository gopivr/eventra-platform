package app.eventra.platform.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "seat_hold", schema = "eventra_booking")
public class SeatHold {
  @Id private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "event_id", nullable = false)
  private UUID eventId;

  @Column(nullable = false)
  private String status = "ACTIVE";

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected SeatHold() {}

  public SeatHold(UUID id, UUID user, UUID event, Instant expires) {
    this.id = id;
    userId = user;
    eventId = event;
    expiresAt = expires;
    createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public UUID getEventId() {
    return eventId;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public String getStatus() {
    return status;
  }

  public void release() {
    if ("ACTIVE".equals(status)) status = "RELEASED";
  }

  public void expire() {
    if ("ACTIVE".equals(status)) status = "EXPIRED";
  }
}

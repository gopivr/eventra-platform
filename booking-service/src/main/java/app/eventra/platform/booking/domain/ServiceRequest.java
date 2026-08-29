package app.eventra.platform.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "service_request", schema = "eventra_booking")
public class ServiceRequest {
  @Id private UUID id;

  @Column(name = "event_id", nullable = false)
  private UUID eventId;

  @Column(name = "requester_user_id", nullable = false)
  private UUID requesterUserId;

  @Column(nullable = false)
  private String state = "OPEN";

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(nullable = false, columnDefinition = "jsonb")
  private String details;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected ServiceRequest() {}

  public ServiceRequest(UUID id, UUID event, UUID requester, String details, Instant expires) {
    this.id = id;
    eventId = event;
    requesterUserId = requester;
    this.details = details;
    expiresAt = expires;
    createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public String getState() {
    return state;
  }

  public void quote() {
    if (Instant.now().isAfter(expiresAt)) throw new IllegalStateException("request expired");
    state = "QUOTING";
  }
}

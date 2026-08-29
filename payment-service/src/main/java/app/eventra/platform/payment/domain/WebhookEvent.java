package app.eventra.platform.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "webhook_event",
    schema = "eventra_payment",
    uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_event_id"}))
public class WebhookEvent {
  @Id private UUID id;

  @Column(nullable = false)
  private String provider;

  @Column(name = "provider_event_id", nullable = false)
  private String providerEventId;

  @Column(name = "signature_verified", nullable = false)
  private boolean signatureVerified;

  @Column(nullable = false, columnDefinition = "jsonb")
  private String payload;

  @Column(name = "received_at", nullable = false)
  private Instant receivedAt;

  protected WebhookEvent() {}

  public WebhookEvent(UUID id, String provider, String eventId, String payload) {
    this.id = id;
    this.provider = provider;
    providerEventId = eventId;
    signatureVerified = true;
    this.payload = payload;
    receivedAt = Instant.now();
  }
}

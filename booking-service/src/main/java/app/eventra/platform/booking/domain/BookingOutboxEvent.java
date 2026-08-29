package app.eventra.platform.booking.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_event", schema = "eventra_booking")
public class BookingOutboxEvent {
  @Id private UUID id;

  @Column(name = "aggregate_type", nullable = false)
  private String aggregateType;

  @Column(name = "aggregate_id", nullable = false)
  private String aggregateId;

  @Column(name = "event_type", nullable = false)
  private String eventType;

  @Column(name = "event_version", nullable = false)
  private int eventVersion = 1;

  @Column(nullable = false, columnDefinition = "jsonb")
  private String payload;

  @Column(name = "occurred_at", nullable = false)
  private Instant occurredAt;

  @Column(name = "published_at")
  private Instant publishedAt;

  @Column(name = "retry_count", nullable = false)
  private int retryCount;

  @Column(name = "last_error")
  private String lastError;

  protected BookingOutboxEvent() {}

  public BookingOutboxEvent(UUID id, String aggregateId, String payload) {
    this.id = id;
    aggregateType = "TicketOrder";
    this.aggregateId = aggregateId;
    eventType = "TicketIssuedV1";
    this.payload = payload;
    occurredAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public String getPayload() {
    return payload;
  }

  public int getRetryCount() {
    return retryCount;
  }

  public void published() {
    publishedAt = Instant.now();
    lastError = null;
  }

  public void failed(Throwable failure) {
    retryCount++;
    lastError = truncate(failure.getMessage());
  }

  public void deadLettered(Throwable failure) {
    failed(failure);
    publishedAt = Instant.now();
  }

  private String truncate(String value) {
    if (value == null) return "unknown publish failure";
    return value.substring(0, Math.min(2000, value.length()));
  }
}

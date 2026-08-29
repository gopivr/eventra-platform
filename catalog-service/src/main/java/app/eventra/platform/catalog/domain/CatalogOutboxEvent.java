package app.eventra.platform.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_event", schema = "eventra_catalog")
public class CatalogOutboxEvent {
  @Id private UUID id;

  @Column(name = "aggregate_type", nullable = false)
  private String aggregateType;

  @Column(name = "aggregate_id", nullable = false)
  private String aggregateId;

  @Column(name = "event_type", nullable = false)
  private String eventType;

  @Column(name = "event_version", nullable = false)
  private int eventVersion;

  @Column(nullable = false, columnDefinition = "jsonb")
  private String payload;

  @Column(name = "occurred_at", nullable = false)
  private Instant occurredAt;

  @Column(name = "retry_count", nullable = false)
  private int retryCount;

  protected CatalogOutboxEvent() {}

  public CatalogOutboxEvent(UUID id, String type, String aggregate, String event, String payload) {
    this.id = id;
    aggregateType = type;
    aggregateId = aggregate;
    eventType = event;
    eventVersion = 1;
    this.payload = payload;
    occurredAt = Instant.now();
  }
}

package app.eventra.platform.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "event_media", schema = "eventra_catalog")
public class EventMedia {
  @Id private UUID id;

  @Column(name = "event_id", nullable = false)
  private UUID eventId;

  @Column(name = "object_storage_key", nullable = false)
  private String objectStorageKey;

  @Column(name = "media_type", nullable = false)
  private String mediaType;

  @Column(name = "sort_order", nullable = false)
  private int sortOrder;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected EventMedia() {}

  public EventMedia(UUID id, UUID event, String key, String type, int order) {
    this.id = id;
    eventId = event;
    objectStorageKey = key;
    mediaType = type;
    sortOrder = order;
    createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public UUID getEventId() {
    return eventId;
  }

  public String getObjectStorageKey() {
    return objectStorageKey;
  }

  public String getMediaType() {
    return mediaType;
  }

  public int getSortOrder() {
    return sortOrder;
  }
}

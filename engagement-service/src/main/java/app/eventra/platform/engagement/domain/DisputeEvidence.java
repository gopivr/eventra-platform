package app.eventra.platform.engagement.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "dispute_evidence", schema = "eventra_engagement")
public class DisputeEvidence {
  @Id private UUID id;

  @Column(name = "dispute_id", nullable = false)
  private UUID disputeId;

  @Column(name = "object_storage_key", nullable = false)
  private String objectStorageKey;

  @Column(name = "content_type", nullable = false)
  private String contentType;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected DisputeEvidence() {}

  public DisputeEvidence(UUID id, UUID dispute, String key, String type) {
    this.id = id;
    disputeId = dispute;
    objectStorageKey = key;
    contentType = type;
    createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public UUID getDisputeId() {
    return disputeId;
  }

  public String getObjectStorageKey() {
    return objectStorageKey;
  }

  public String getContentType() {
    return contentType;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}

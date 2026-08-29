package app.eventra.platform.identity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "verification_document_reference", schema = "eventra_identity")
public class VerificationDocumentReference {
  @Id private UUID id;

  @Column(name = "case_id", nullable = false)
  private UUID caseId;

  @Column(name = "object_storage_key", nullable = false)
  private String objectStorageKey;

  @Column(name = "document_type", nullable = false)
  private String documentType;

  @Column(name = "content_type", nullable = false)
  private String contentType;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected VerificationDocumentReference() {}

  public VerificationDocumentReference(
      UUID id, UUID caseId, String key, String type, String content) {
    this.id = id;
    this.caseId = caseId;
    objectStorageKey = key;
    documentType = type;
    contentType = content;
    createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public UUID getCaseId() {
    return caseId;
  }

  public String getObjectStorageKey() {
    return objectStorageKey;
  }

  public String getDocumentType() {
    return documentType;
  }

  public String getContentType() {
    return contentType;
  }
}

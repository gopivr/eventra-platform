package app.eventra.platform.engagement.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "support_case_message", schema = "eventra_engagement")
public class SupportCaseMessage {
  @Id private UUID id;

  @Column(name = "case_id", nullable = false)
  private UUID caseId;

  @Column(name = "author_user_id", nullable = false)
  private UUID authorUserId;

  @Column(nullable = false)
  private String body;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected SupportCaseMessage() {}

  public SupportCaseMessage(UUID id, UUID caseId, UUID author, String body) {
    this.id = id;
    this.caseId = caseId;
    authorUserId = author;
    this.body = body.trim();
    createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public UUID getCaseId() {
    return caseId;
  }

  public UUID getAuthorUserId() {
    return authorUserId;
  }

  public String getBody() {
    return body;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}

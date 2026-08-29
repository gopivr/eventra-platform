package app.eventra.platform.engagement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "support_case", schema = "eventra_engagement")
public class SupportCase {
  @Id private UUID id;

  @Column(name = "requester_user_id", nullable = false)
  private UUID requesterUserId;

  @Column(nullable = false)
  private String status = "OPEN";

  @Column(nullable = false)
  private String subject;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected SupportCase() {}

  public SupportCase(UUID id, UUID user, String subject) {
    this.id = id;
    requesterUserId = user;
    this.subject = subject;
    createdAt = Instant.now();
    updatedAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getRequesterUserId() {
    return requesterUserId;
  }

  public String getStatus() {
    return status;
  }

  public String getSubject() {
    return subject;
  }
}

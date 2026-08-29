package app.eventra.platform.identity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "verification_case", schema = "eventra_identity")
public class VerificationCase {
  @Id private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "case_type", nullable = false)
  private String caseType;

  @Column(nullable = false)
  private String status = "SUBMITTED";

  @Column(name = "submitted_at")
  private Instant submittedAt;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected VerificationCase() {}

  public VerificationCase(UUID id, UUID user, String type) {
    this.id = id;
    userId = user;
    caseType = type;
    status = "SUBMITTED";
    createdAt = Instant.now();
    submittedAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getCaseType() {
    return caseType;
  }

  public String getStatus() {
    return status;
  }
}

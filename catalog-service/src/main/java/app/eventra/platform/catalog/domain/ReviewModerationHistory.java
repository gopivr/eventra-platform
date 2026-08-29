package app.eventra.platform.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "review_moderation_history", schema = "eventra_catalog")
public class ReviewModerationHistory {
  @Id private UUID id;

  @Column(name = "review_id", nullable = false)
  private UUID reviewId;

  @Column(name = "from_state")
  private String fromState;

  @Column(name = "to_state", nullable = false)
  private String toState;

  @Column(name = "moderator_subject", nullable = false)
  private String moderatorSubject;

  @Column(name = "changed_at", nullable = false)
  private Instant changedAt;

  protected ReviewModerationHistory() {}

  public ReviewModerationHistory(UUID id, UUID review, String from, String to, String moderator) {
    this.id = id;
    reviewId = review;
    fromState = from;
    toState = to;
    moderatorSubject = moderator;
    changedAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public String getFromState() {
    return fromState;
  }

  public String getToState() {
    return toState;
  }

  public Instant getChangedAt() {
    return changedAt;
  }
}

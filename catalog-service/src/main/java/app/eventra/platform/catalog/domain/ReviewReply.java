package app.eventra.platform.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "review_reply", schema = "eventra_catalog")
public class ReviewReply {
  @Id private UUID id;

  @Column(name = "review_id", nullable = false, unique = true)
  private UUID reviewId;

  @Column(name = "author_organization_id", nullable = false)
  private UUID authorOrganizationId;

  @Column(nullable = false)
  private String body;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected ReviewReply() {}

  public ReviewReply(UUID id, UUID review, UUID author, String body) {
    this.id = id;
    reviewId = review;
    authorOrganizationId = author;
    this.body = body;
    createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public UUID getReviewId() {
    return reviewId;
  }

  public UUID getAuthorOrganizationId() {
    return authorOrganizationId;
  }

  public String getBody() {
    return body;
  }
}

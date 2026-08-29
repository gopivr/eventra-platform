package app.eventra.platform.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "review", schema = "eventra_catalog")
public class Review {
  @Id private UUID id;

  @Column(name = "provider_id")
  private UUID providerId;

  @Column(name = "venue_id")
  private UUID venueId;

  @Column(name = "booking_reference", nullable = false)
  private String bookingReference;

  @Column(name = "author_user_id", nullable = false)
  private UUID authorUserId;

  @Column(nullable = false)
  private short rating;

  @Column(nullable = false)
  private String body;

  @Column(name = "moderation_state", nullable = false)
  private String moderationState = "PENDING";

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected Review() {}

  public Review(
      UUID id, UUID provider, UUID venue, String booking, UUID author, short rating, String body) {
    if ((provider == null) == (venue == null))
      throw new IllegalArgumentException("exactly one review target is required");
    if (rating < 1 || rating > 5)
      throw new IllegalArgumentException("rating must be between 1 and 5");
    this.id = id;
    providerId = provider;
    venueId = venue;
    bookingReference = booking;
    authorUserId = author;
    this.rating = rating;
    this.body = body;
    createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public UUID getProviderId() {
    return providerId;
  }

  public UUID getVenueId() {
    return venueId;
  }

  public short getRating() {
    return rating;
  }

  public String getBody() {
    return body;
  }

  public String getModerationState() {
    return moderationState;
  }

  public void moderate(String state) {
    if (!Set.of("PENDING", "APPROVED", "REJECTED").contains(state))
      throw new IllegalArgumentException("invalid moderation state");
    moderationState = state;
  }
}

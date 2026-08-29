package app.eventra.platform.engagement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "saved_item", schema = "eventra_engagement")
@IdClass(SavedItem.Key.class)
public class SavedItem {
  @Id
  @Column(name = "user_id")
  private java.util.UUID userId;

  @Id
  @Column(name = "item_type")
  private String itemType;

  @Id
  @Column(name = "item_id")
  private String itemId;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected SavedItem() {}

  public SavedItem(java.util.UUID user, String type, String item) {
    userId = user;
    itemType = type;
    itemId = item;
    createdAt = Instant.now();
  }

  public record Key(java.util.UUID userId, String itemType, String itemId) {}
}

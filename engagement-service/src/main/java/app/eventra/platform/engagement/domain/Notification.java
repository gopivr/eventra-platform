package app.eventra.platform.engagement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification", schema = "eventra_engagement")
public class Notification {
  @Id private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "notification_type", nullable = false)
  private String notificationType;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String body;

  @Column(name = "resource_type")
  private String resourceType;

  @Column(name = "resource_id")
  private String resourceId;

  @Column(name = "read_at")
  private Instant readAt;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected Notification() {}

  public Notification(
      UUID id,
      UUID user,
      String type,
      String title,
      String body,
      String resourceType,
      String resourceId) {
    this.id = id;
    userId = user;
    notificationType = type;
    this.title = title;
    this.body = body;
    this.resourceType = resourceType;
    this.resourceId = resourceId;
    createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getTitle() {
    return title;
  }

  public String getBody() {
    return body;
  }

  public boolean isRead() {
    return readAt != null;
  }

  public void markRead() {
    readAt = Instant.now();
  }
}

package app.eventra.platform.identity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_role", schema = "eventra_identity")
@IdClass(UserRole.Key.class)
public class UserRole {
  @Id
  @Column(name = "user_id")
  private UUID userId;

  @Id
  @Column(name = "role_id")
  private UUID roleId;

  @Column(name = "assigned_at", nullable = false)
  private Instant assignedAt;

  @Column(name = "assigned_by")
  private String assignedBy;

  protected UserRole() {}

  public UserRole(UUID userId, UUID roleId, String by) {
    this.userId = userId;
    this.roleId = roleId;
    this.assignedBy = by;
    this.assignedAt = Instant.now();
  }

  public record Key(UUID userId, UUID roleId) {}
}

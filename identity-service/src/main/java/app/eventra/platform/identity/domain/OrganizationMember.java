package app.eventra.platform.identity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "organization_member", schema = "eventra_identity")
@IdClass(OrganizationMember.Key.class)
public class OrganizationMember {
  @Id
  @Column(name = "organization_id")
  private UUID organizationId;

  @Id
  @Column(name = "user_id")
  private UUID userId;

  @Column(name = "membership_role", nullable = false)
  private String membershipRole;

  @Column(name = "joined_at", nullable = false)
  private Instant joinedAt;

  protected OrganizationMember() {}

  public OrganizationMember(UUID org, UUID user, String role) {
    organizationId = org;
    userId = user;
    membershipRole = role;
    joinedAt = Instant.now();
  }

  public record Key(UUID organizationId, UUID userId) {}
}

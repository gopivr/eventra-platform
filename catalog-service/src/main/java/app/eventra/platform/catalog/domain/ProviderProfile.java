package app.eventra.platform.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "provider_profile", schema = "eventra_catalog")
public class ProviderProfile {
  @Id private UUID id;

  @Column(name = "owner_organization_id", nullable = false, unique = true)
  private UUID ownerOrganizationId;

  @Column(name = "display_name", nullable = false)
  private String displayName;

  @Column(name = "verification_status", nullable = false)
  private String verificationStatus = "PENDING";

  @Column(nullable = false)
  private boolean active = true;

  private String bio;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected ProviderProfile() {}

  public ProviderProfile(UUID id, UUID owner, String name, String bio) {
    this.id = id;
    ownerOrganizationId = owner;
    displayName = name;
    this.bio = bio;
    createdAt = Instant.now();
    updatedAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getOwnerOrganizationId() {
    return ownerOrganizationId;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getVerificationStatus() {
    return verificationStatus;
  }

  public boolean isActive() {
    return active;
  }
}

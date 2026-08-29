package app.eventra.platform.identity.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "organization", schema = "eventra_identity")
public class Organization {
  @Id private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String status = "ACTIVE";

  @Column(name = "created_by", nullable = false)
  private UUID createdBy;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected Organization() {}

  public Organization(UUID id, String name, UUID createdBy) {
    this.id = id;
    this.name = name;
    this.createdBy = createdBy;
    this.createdAt = Instant.now();
    this.updatedAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public UUID getCreatedBy() {
    return createdBy;
  }
}

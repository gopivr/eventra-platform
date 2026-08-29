package app.eventra.platform.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "venue", schema = "eventra_catalog")
public class Venue {
  @Id private UUID id;

  @Column(name = "owner_organization_id", nullable = false)
  private UUID ownerOrganizationId;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String status = "ACTIVE";

  @Column(nullable = false)
  private String city;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(nullable = false, columnDefinition = "jsonb")
  private String address;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Version private long version;

  protected Venue() {}

  public Venue(UUID id, UUID owner, String name, String city, String address) {
    this.id = id;
    ownerOrganizationId = owner;
    this.name = name;
    this.city = city;
    this.address = address;
    createdAt = Instant.now();
    updatedAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getOwnerOrganizationId() {
    return ownerOrganizationId;
  }

  public String getName() {
    return name;
  }

  public String getStatus() {
    return status;
  }

  public String getCity() {
    return city;
  }

  public String getAddress() {
    return address;
  }
}

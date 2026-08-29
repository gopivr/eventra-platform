package app.eventra.platform.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "portfolio_item", schema = "eventra_catalog")
public class PortfolioItem {
  @Id private UUID id;

  @Column(name = "provider_id", nullable = false)
  private UUID providerId;

  @Column(name = "object_storage_key", nullable = false)
  private String objectStorageKey;

  private String caption;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected PortfolioItem() {}

  public PortfolioItem(UUID id, UUID provider, String key, String caption) {
    this.id = id;
    providerId = provider;
    objectStorageKey = key;
    this.caption = caption;
    createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public UUID getProviderId() {
    return providerId;
  }

  public String getObjectStorageKey() {
    return objectStorageKey;
  }

  public String getCaption() {
    return caption;
  }
}

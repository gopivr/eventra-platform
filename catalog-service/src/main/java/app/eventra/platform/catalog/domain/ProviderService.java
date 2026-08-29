package app.eventra.platform.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "provider_service", schema = "eventra_catalog")
public class ProviderService {
  @Id private UUID id;

  @Column(name = "provider_id", nullable = false)
  private UUID providerId;

  @Column(nullable = false)
  private String name;

  @Column(name = "pricing_model", nullable = false)
  private String pricingModel;

  @Column(name = "base_amount_minor", nullable = false)
  private long baseAmountMinor;

  @Column(nullable = false)
  private String currency;

  @Column(nullable = false)
  private boolean active = true;

  protected ProviderService() {}

  public ProviderService(
      UUID id, UUID provider, String name, String model, long amount, String currency) {
    if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
    this.id = id;
    providerId = provider;
    this.name = name;
    pricingModel = model;
    baseAmountMinor = amount;
    this.currency = currency;
  }

  public UUID getId() {
    return id;
  }

  public UUID getProviderId() {
    return providerId;
  }

  public String getName() {
    return name;
  }

  public String getPricingModel() {
    return pricingModel;
  }

  public long getBaseAmountMinor() {
    return baseAmountMinor;
  }

  public String getCurrency() {
    return currency;
  }
}

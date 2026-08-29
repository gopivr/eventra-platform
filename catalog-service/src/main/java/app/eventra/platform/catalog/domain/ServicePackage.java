package app.eventra.platform.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "service_package", schema = "eventra_catalog")
public class ServicePackage {
  @Id private UUID id;

  @Column(name = "provider_service_id", nullable = false)
  private UUID providerServiceId;

  @Column(nullable = false)
  private String name;

  @Column(name = "amount_minor", nullable = false)
  private long amountMinor;

  @Column(nullable = false)
  private String currency;

  @Column(nullable = false)
  private boolean active = true;

  protected ServicePackage() {}

  public ServicePackage(UUID id, UUID service, String name, long amount, String currency) {
    if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
    this.id = id;
    providerServiceId = service;
    this.name = name;
    amountMinor = amount;
    this.currency = currency;
  }

  public UUID getId() {
    return id;
  }

  public UUID getProviderServiceId() {
    return providerServiceId;
  }

  public String getName() {
    return name;
  }

  public long getAmountMinor() {
    return amountMinor;
  }

  public String getCurrency() {
    return currency;
  }
}

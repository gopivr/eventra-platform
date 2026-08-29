package app.eventra.platform.payment.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "settlement", schema = "eventra_payment")
public class Settlement {
  @Id private UUID id;

  @Column(name = "beneficiary_reference", nullable = false)
  private String beneficiaryReference;

  @Column(nullable = false)
  private String status = "READY";

  @Column(nullable = false)
  private String currency;

  @Column(name = "amount_minor", nullable = false)
  private long amountMinor;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected Settlement() {}

  public Settlement(UUID id, String beneficiary, String currency, long amount) {
    if (amount <= 0) throw new IllegalArgumentException("settlement amount must be positive");
    this.id = id;
    beneficiaryReference = beneficiary;
    this.currency = currency;
    amountMinor = amount;
    createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public String getBeneficiaryReference() {
    return beneficiaryReference;
  }

  public String getStatus() {
    return status;
  }

  public String getCurrency() {
    return currency;
  }

  public long getAmountMinor() {
    return amountMinor;
  }

  public void markPaying() {
    if (!"READY".equals(status)) throw new IllegalStateException("settlement is not payable");
    status = "PAYING";
  }

  public void paid() {
    status = "PAID";
  }
}

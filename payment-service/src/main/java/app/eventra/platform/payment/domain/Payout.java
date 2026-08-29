package app.eventra.platform.payment.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "payout",
    schema = "eventra_payment",
    uniqueConstraints =
        @UniqueConstraint(columnNames = {"beneficiary_reference", "idempotency_key"}))
public class Payout {
  @Id private UUID id;

  @Column(name = "settlement_id", nullable = false)
  private UUID settlementId;

  @Column(name = "beneficiary_reference", nullable = false)
  private String beneficiaryReference;

  @Column(name = "idempotency_key", nullable = false)
  private String idempotencyKey;

  @Column(name = "provider_reference")
  private String providerReference;

  @Column(nullable = false)
  private String status = "PENDING";

  @Column(name = "fee_minor", nullable = false)
  private long feeMinor;

  @Column(name = "requested_at", nullable = false)
  private Instant requestedAt;

  protected Payout() {}

  public Payout(UUID id, UUID settlement, String beneficiary, String key) {
    this.id = id;
    settlementId = settlement;
    beneficiaryReference = beneficiary;
    idempotencyKey = key;
    requestedAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public UUID getSettlementId() {
    return settlementId;
  }

  public String getBeneficiaryReference() {
    return beneficiaryReference;
  }

  public String getStatus() {
    return status;
  }

  public String getProviderReference() {
    return providerReference;
  }

  public void submit(String reference, long fee) {
    if (fee < 0) throw new IllegalArgumentException("payout fee must not be negative");
    providerReference = reference;
    feeMinor = fee;
    status = "SUBMITTED";
  }

  public void complete() {
    status = "COMPLETED";
  }

  public void fail() {
    status = "FAILED";
  }
}

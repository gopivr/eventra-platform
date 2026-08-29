package app.eventra.platform.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refund", schema = "eventra_payment")
public class Refund {
  @Id private UUID id;

  @Column(name = "payment_intent_id", nullable = false)
  private UUID paymentIntentId;

  @Column(name = "idempotency_key", nullable = false)
  private String idempotencyKey;

  @Column(name = "amount_minor", nullable = false)
  private long amountMinor;

  @Column(nullable = false)
  private String currency;

  @Column(nullable = false)
  private String status = "PENDING";

  @Column(name = "requested_at", nullable = false)
  private Instant requestedAt;

  protected Refund() {}

  public Refund(UUID id, UUID intent, String key, long amount, String currency) {
    if (amount <= 0) throw new IllegalArgumentException("refund amount must be positive");
    this.id = id;
    paymentIntentId = intent;
    idempotencyKey = key;
    amountMinor = amount;
    this.currency = currency;
    requestedAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public long getAmountMinor() {
    return amountMinor;
  }

  public String getStatus() {
    return status;
  }

  public void complete() {
    status = "COMPLETED";
  }
}

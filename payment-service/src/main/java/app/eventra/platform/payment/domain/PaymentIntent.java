package app.eventra.platform.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_intent", schema = "eventra_payment")
public class PaymentIntent {
  @Id private UUID id;

  @Column(name = "payer_reference", nullable = false)
  private String payerReference;

  @Column(name = "order_reference", nullable = false)
  private String orderReference;

  @Column(name = "amount_minor", nullable = false)
  private long amountMinor;

  @Column(nullable = false)
  private String currency;

  @Column(nullable = false)
  private String status = "REQUIRES_ACTION";

  @Column(name = "idempotency_key", nullable = false)
  private String idempotencyKey;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected PaymentIntent() {}

  public PaymentIntent(
      UUID id, String payer, String order, long amount, String currency, String key) {
    if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
    this.id = id;
    payerReference = payer;
    orderReference = order;
    amountMinor = amount;
    this.currency = currency;
    idempotencyKey = key;
    createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public String getPayerReference() {
    return payerReference;
  }

  public String getOrderReference() {
    return orderReference;
  }

  public long getAmountMinor() {
    return amountMinor;
  }

  public String getCurrency() {
    return currency;
  }

  public String getStatus() {
    return status;
  }

  public void capture() {
    if (!"REQUIRES_ACTION".equals(status))
      throw new IllegalStateException("payment is not capturable");
    status = "CAPTURED";
  }
}

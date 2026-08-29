package app.eventra.platform.payment.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_attempt", schema = "eventra_payment")
public class PaymentAttempt {
  @Id private UUID id;

  @Column(name = "payment_intent_id", nullable = false)
  private UUID paymentIntentId;

  @Column(nullable = false)
  private String provider;

  @Column(name = "provider_payment_reference")
  private String providerPaymentReference;

  @Column(nullable = false)
  private String status;

  @Column(name = "amount_minor", nullable = false)
  private long amountMinor;

  @Column(name = "attempted_at", nullable = false)
  private Instant attemptedAt;

  protected PaymentAttempt() {}

  public PaymentAttempt(
      UUID id, UUID intentId, String provider, String reference, String status, long amount) {
    this.id = id;
    paymentIntentId = intentId;
    this.provider = provider;
    providerPaymentReference = reference;
    this.status = status;
    amountMinor = amount;
    attemptedAt = Instant.now();
  }
}

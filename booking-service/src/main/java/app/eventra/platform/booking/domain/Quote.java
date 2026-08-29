package app.eventra.platform.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "quote", schema = "eventra_booking")
public class Quote {
  @Id private UUID id;

  @Column(name = "request_id", nullable = false)
  private UUID requestId;

  @Column(name = "provider_id", nullable = false)
  private UUID providerId;

  @Column(name = "amount_minor", nullable = false)
  private long amountMinor;

  @Column(nullable = false)
  private String currency;

  @Column(nullable = false)
  private String status = "SUBMITTED";

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected Quote() {}

  public Quote(
      UUID id, UUID request, UUID provider, long amount, String currency, Instant expires) {
    if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
    this.id = id;
    requestId = request;
    providerId = provider;
    amountMinor = amount;
    this.currency = currency;
    expiresAt = expires;
    createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public long getAmountMinor() {
    return amountMinor;
  }
}

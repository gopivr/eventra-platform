package app.eventra.platform.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "ticket_order",
    schema = "eventra_booking",
    uniqueConstraints =
        @UniqueConstraint(
            name = "ticket_order_user_idempotency",
            columnNames = {"user_id", "idempotency_key"}))
public class TicketOrder {
  @Id private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "event_id", nullable = false)
  private UUID eventId;

  @Column(name = "idempotency_key", nullable = false)
  private String idempotencyKey;

  @Column(nullable = false)
  private String status = "PENDING_PAYMENT";

  @Column(nullable = false)
  private String currency;

  @Column(name = "subtotal_minor", nullable = false)
  private long subtotalMinor;

  @Column(name = "tax_minor", nullable = false)
  private long taxMinor;

  @Column(name = "fee_minor", nullable = false)
  private long feeMinor;

  @Column(name = "total_minor", nullable = false)
  private long totalMinor;

  @Column(name = "pricing_snapshot", nullable = false, columnDefinition = "jsonb")
  private String pricingSnapshot;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Version private long version;

  protected TicketOrder() {}

  public TicketOrder(
      UUID id,
      UUID user,
      UUID event,
      String key,
      String currency,
      long subtotal,
      long tax,
      long fee,
      String snapshot) {
    if (subtotal < 0 || tax < 0 || fee < 0)
      throw new IllegalArgumentException("amounts must not be negative");
    this.id = id;
    userId = user;
    eventId = event;
    idempotencyKey = key;
    this.currency = currency;
    subtotalMinor = subtotal;
    taxMinor = tax;
    feeMinor = fee;
    totalMinor = subtotal + tax + fee;
    pricingSnapshot = snapshot;
    createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public UUID getUserId() {
    return userId;
  }

  public UUID getEventId() {
    return eventId;
  }

  public String getStatus() {
    return status;
  }

  public long getTotalMinor() {
    return totalMinor;
  }

  public void confirm() {
    if (!"PENDING_PAYMENT".equals(status))
      throw new IllegalStateException("order is not awaiting payment");
    status = "CONFIRMED";
  }
}

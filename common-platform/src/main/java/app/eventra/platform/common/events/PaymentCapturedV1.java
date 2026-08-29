package app.eventra.platform.common.events;

import java.time.Instant;
import java.util.UUID;

public record PaymentCapturedV1(
    UUID messageId,
    UUID paymentIntentId,
    UUID userId,
    String orderId,
    long amountMinor,
    String currency,
    Instant occurredAt) {
  public PaymentCapturedV1 {
    if (messageId == null
        || paymentIntentId == null
        || userId == null
        || orderId == null
        || occurredAt == null)
      throw new IllegalArgumentException("payment event fields are required");
  }
}

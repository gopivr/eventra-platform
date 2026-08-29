package app.eventra.platform.common.events;

import java.time.Instant;
import java.util.UUID;

public record TicketIssuedV1(
    UUID messageId, UUID orderId, UUID ticketId, UUID userId, UUID eventId, Instant occurredAt) {
  public TicketIssuedV1 {
    if (messageId == null
        || orderId == null
        || ticketId == null
        || userId == null
        || eventId == null
        || occurredAt == null)
      throw new IllegalArgumentException("ticket event fields are required");
  }
}

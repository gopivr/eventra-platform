package app.eventra.platform.engagement.service;

import app.eventra.platform.common.events.TicketIssuedV1;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TicketIssuedListener {
  private final NotificationService notifications;
  private final ObjectMapper json;

  public TicketIssuedListener(NotificationService notifications, ObjectMapper json) {
    this.notifications = notifications;
    this.json = json;
  }

  @KafkaListener(topics = "eventra.booking", groupId = "engagement-service")
  public void onTicketIssued(String payload) throws Exception {
    TicketIssuedV1 event = json.readValue(payload, TicketIssuedV1.class);
    notifications.ticketIssued(
        event.messageId().toString(), event.userId(), event.orderId().toString(), event.ticketId());
  }
}

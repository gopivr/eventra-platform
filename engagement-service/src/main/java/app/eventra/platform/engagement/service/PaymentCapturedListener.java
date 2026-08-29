package app.eventra.platform.engagement.service;

import app.eventra.platform.common.events.PaymentCapturedV1;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentCapturedListener {
  private final NotificationService notifications;
  private final ObjectMapper json;

  public PaymentCapturedListener(NotificationService notifications, ObjectMapper json) {
    this.notifications = notifications;
    this.json = json;
  }

  @KafkaListener(topics = "eventra.payment", groupId = "engagement-service")
  public void onPaymentCaptured(String payload) throws Exception {
    PaymentCapturedV1 event = json.readValue(payload, PaymentCapturedV1.class);
    notifications.paymentCaptured(event.messageId().toString(), event.userId(), event.orderId());
  }
}

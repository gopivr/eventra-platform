package app.eventra.platform.booking.service;

import app.eventra.platform.common.events.PaymentCapturedV1;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentCapturedListener {
  private final BookingService booking;
  private final ObjectMapper json;

  public PaymentCapturedListener(BookingService booking, ObjectMapper json) {
    this.booking = booking;
    this.json = json;
  }

  @KafkaListener(topics = "eventra.payment", groupId = "booking-service")
  public void onPaymentCaptured(String payload) throws Exception {
    PaymentCapturedV1 event = json.readValue(payload, PaymentCapturedV1.class);
    booking.confirmAndIssue(java.util.UUID.fromString(event.orderId()));
  }
}

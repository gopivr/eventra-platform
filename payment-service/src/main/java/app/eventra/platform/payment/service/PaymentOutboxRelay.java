package app.eventra.platform.payment.service;

import app.eventra.platform.payment.repository.PaymentOutboxEventRepository;
import java.util.concurrent.TimeUnit;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PaymentOutboxRelay {
  private static final int MAX_ATTEMPTS = 10;
  private final PaymentOutboxEventRepository outbox;
  private final KafkaTemplate<String, String> kafka;

  public PaymentOutboxRelay(
      PaymentOutboxEventRepository outbox, KafkaTemplate<String, String> kafka) {
    this.outbox = outbox;
    this.kafka = kafka;
  }

  @Scheduled(fixedDelayString = "${payment.outbox-relay-delay:1000}")
  @Transactional
  public void publish() {
    outbox
        .findTop100ByPublishedAtIsNullOrderByOccurredAtAsc()
        .forEach(
            event -> {
              try {
                String topic =
                    event.getRetryCount() >= MAX_ATTEMPTS - 1
                        ? "eventra.payment.DLQ"
                        : "eventra.payment";
                kafka
                    .send(topic, event.getId().toString(), event.getPayload())
                    .get(10, TimeUnit.SECONDS);
                if (topic.endsWith(".DLQ"))
                  event.deadLettered(new IllegalStateException("publish retries exhausted"));
                else event.published();
                outbox.save(event);
              } catch (Exception failure) {
                event.failed(failure);
                outbox.save(event);
              }
            });
  }
}

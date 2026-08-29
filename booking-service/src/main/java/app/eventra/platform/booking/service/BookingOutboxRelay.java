package app.eventra.platform.booking.service;

import app.eventra.platform.booking.repository.BookingOutboxEventRepository;
import java.util.concurrent.TimeUnit;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BookingOutboxRelay {
  private static final int MAX_ATTEMPTS = 10;
  private final BookingOutboxEventRepository outbox;
  private final KafkaTemplate<String, String> kafka;

  public BookingOutboxRelay(
      BookingOutboxEventRepository outbox, KafkaTemplate<String, String> kafka) {
    this.outbox = outbox;
    this.kafka = kafka;
  }

  @Scheduled(fixedDelayString = "${booking.outbox-relay-delay:1000}")
  @Transactional
  public void publish() {
    outbox
        .findTop100ByPublishedAtIsNullOrderByOccurredAtAsc()
        .forEach(
            event -> {
              try {
                String topic =
                    event.getRetryCount() >= MAX_ATTEMPTS - 1
                        ? "eventra.booking.DLQ"
                        : "eventra.booking";
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

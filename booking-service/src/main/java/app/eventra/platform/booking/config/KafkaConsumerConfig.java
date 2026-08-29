package app.eventra.platform.booking.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.*;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {
  @Bean
  DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String, String> kafka) {
    DeadLetterPublishingRecoverer recoverer =
        new DeadLetterPublishingRecoverer(
            kafka,
            (record, error) -> new TopicPartition(record.topic() + ".DLQ", record.partition()));
    return new DefaultErrorHandler(recoverer, new FixedBackOff(1000, 4));
  }
}

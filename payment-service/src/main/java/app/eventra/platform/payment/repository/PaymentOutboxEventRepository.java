package app.eventra.platform.payment.repository;

import app.eventra.platform.payment.domain.PaymentOutboxEvent;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentOutboxEventRepository extends JpaRepository<PaymentOutboxEvent, UUID> {
  List<PaymentOutboxEvent> findTop100ByPublishedAtIsNullOrderByOccurredAtAsc();
}

package app.eventra.platform.payment.repository;

import app.eventra.platform.payment.domain.Refund;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<Refund, UUID> {
  Optional<Refund> findByPaymentIntentIdAndIdempotencyKey(
      UUID paymentIntentId, String idempotencyKey);
}

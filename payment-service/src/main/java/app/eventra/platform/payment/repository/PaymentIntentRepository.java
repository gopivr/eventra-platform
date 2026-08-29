package app.eventra.platform.payment.repository;

import app.eventra.platform.payment.domain.PaymentIntent;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentIntentRepository extends JpaRepository<PaymentIntent, UUID> {
  Optional<PaymentIntent> findByPayerReferenceAndIdempotencyKey(
      String payerReference, String idempotencyKey);
}

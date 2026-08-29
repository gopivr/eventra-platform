package app.eventra.platform.payment.repository;

import app.eventra.platform.payment.domain.Payout;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayoutRepository extends JpaRepository<Payout, UUID> {
  Optional<Payout> findByBeneficiaryReferenceAndIdempotencyKey(String beneficiary, String key);

  List<Payout> findByStatus(String status);

  Optional<Payout> findByProviderReference(String reference);
}

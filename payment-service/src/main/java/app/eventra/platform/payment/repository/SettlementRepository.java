package app.eventra.platform.payment.repository;

import app.eventra.platform.payment.domain.Settlement;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, UUID> {}

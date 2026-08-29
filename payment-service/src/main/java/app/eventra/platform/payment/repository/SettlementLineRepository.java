package app.eventra.platform.payment.repository;

import app.eventra.platform.payment.domain.SettlementLine;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementLineRepository extends JpaRepository<SettlementLine, UUID> {}

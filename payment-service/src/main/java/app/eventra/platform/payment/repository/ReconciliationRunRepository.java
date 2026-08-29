package app.eventra.platform.payment.repository;

import app.eventra.platform.payment.domain.ReconciliationRun;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReconciliationRunRepository extends JpaRepository<ReconciliationRun, UUID> {}

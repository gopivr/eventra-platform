package app.eventra.platform.identity.repository;

import app.eventra.platform.identity.domain.VerificationCase;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCaseRepository extends JpaRepository<VerificationCase, UUID> {
  List<VerificationCase> findByUserIdOrderByCreatedAtDesc(UUID userId);
}

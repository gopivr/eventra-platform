package app.eventra.platform.engagement.repository;

import app.eventra.platform.engagement.domain.SupportCase;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportCaseRepository extends JpaRepository<SupportCase, UUID> {
  List<SupportCase> findByRequesterUserId(UUID userId);
}

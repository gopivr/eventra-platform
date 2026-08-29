package app.eventra.platform.engagement.repository;

import app.eventra.platform.engagement.domain.SupportCaseMessage;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportCaseMessageRepository extends JpaRepository<SupportCaseMessage, UUID> {
  List<SupportCaseMessage> findByCaseIdOrderByCreatedAtAsc(UUID caseId);
}

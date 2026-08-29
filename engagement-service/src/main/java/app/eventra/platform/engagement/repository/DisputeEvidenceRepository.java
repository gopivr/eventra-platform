package app.eventra.platform.engagement.repository;

import app.eventra.platform.engagement.domain.DisputeEvidence;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisputeEvidenceRepository extends JpaRepository<DisputeEvidence, UUID> {
  List<DisputeEvidence> findByDisputeIdOrderByCreatedAtAsc(UUID disputeId);
}

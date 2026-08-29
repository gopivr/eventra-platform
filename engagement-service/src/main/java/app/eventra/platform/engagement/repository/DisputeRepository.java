package app.eventra.platform.engagement.repository;

import app.eventra.platform.engagement.domain.Dispute;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisputeRepository extends JpaRepository<Dispute, UUID> {
  List<Dispute> findByOpenedByUserId(UUID userId);
}

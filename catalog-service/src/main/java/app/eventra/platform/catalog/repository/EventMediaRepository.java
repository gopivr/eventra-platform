package app.eventra.platform.catalog.repository;

import app.eventra.platform.catalog.domain.EventMedia;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventMediaRepository extends JpaRepository<EventMedia, UUID> {
  List<EventMedia> findByEventIdOrderBySortOrder(UUID eventId);
}

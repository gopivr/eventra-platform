package app.eventra.platform.engagement.repository;

import app.eventra.platform.engagement.domain.SavedItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedItemRepository extends JpaRepository<SavedItem, SavedItem.Key> {
  List<SavedItem> findByUserId(UUID userId);
}

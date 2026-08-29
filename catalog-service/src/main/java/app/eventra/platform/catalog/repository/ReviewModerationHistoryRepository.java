package app.eventra.platform.catalog.repository;

import app.eventra.platform.catalog.domain.ReviewModerationHistory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewModerationHistoryRepository
    extends JpaRepository<ReviewModerationHistory, UUID> {
  List<ReviewModerationHistory> findByReviewIdOrderByChangedAtDesc(UUID reviewId);
}

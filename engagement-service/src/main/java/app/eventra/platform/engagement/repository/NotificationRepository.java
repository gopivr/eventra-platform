package app.eventra.platform.engagement.repository;

import app.eventra.platform.engagement.domain.Notification;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
  List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId);

  Optional<Notification> findByIdAndUserId(UUID id, UUID userId);
}

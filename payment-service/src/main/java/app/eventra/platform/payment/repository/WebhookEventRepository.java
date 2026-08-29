package app.eventra.platform.payment.repository;

import app.eventra.platform.payment.domain.WebhookEvent;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, UUID> {
  boolean existsByProviderAndProviderEventId(String provider, String providerEventId);
}

package app.eventra.platform.booking.repository;

import app.eventra.platform.booking.domain.ProviderAvailability;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderAvailabilityRepository extends JpaRepository<ProviderAvailability, UUID> {
  List<ProviderAvailability> findByProviderId(UUID providerId);
}

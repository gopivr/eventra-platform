package app.eventra.platform.catalog.repository;

import app.eventra.platform.catalog.domain.ProviderService;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderServiceRepository extends JpaRepository<ProviderService, UUID> {
  List<ProviderService> findByProviderIdAndActiveTrue(UUID providerId);
}

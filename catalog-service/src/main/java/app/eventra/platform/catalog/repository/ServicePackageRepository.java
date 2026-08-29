package app.eventra.platform.catalog.repository;

import app.eventra.platform.catalog.domain.ServicePackage;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicePackageRepository extends JpaRepository<ServicePackage, UUID> {
  List<ServicePackage> findByProviderServiceIdAndActiveTrue(UUID providerServiceId);
}

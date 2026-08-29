package app.eventra.platform.catalog.repository;

import app.eventra.platform.catalog.domain.ProviderProfile;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderProfileRepository extends JpaRepository<ProviderProfile, UUID> {
  List<ProviderProfile> findByActiveTrueAndVerificationStatus(String status);
}

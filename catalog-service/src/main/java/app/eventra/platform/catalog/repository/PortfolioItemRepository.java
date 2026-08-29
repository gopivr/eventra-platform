package app.eventra.platform.catalog.repository;

import app.eventra.platform.catalog.domain.PortfolioItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, UUID> {
  List<PortfolioItem> findByProviderId(UUID providerId);
}

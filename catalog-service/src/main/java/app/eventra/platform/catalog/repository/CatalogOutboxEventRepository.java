package app.eventra.platform.catalog.repository;

import app.eventra.platform.catalog.domain.CatalogOutboxEvent;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogOutboxEventRepository extends JpaRepository<CatalogOutboxEvent, UUID> {}

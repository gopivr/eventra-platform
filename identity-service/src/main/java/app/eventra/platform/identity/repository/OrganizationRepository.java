package app.eventra.platform.identity.repository;

import app.eventra.platform.identity.domain.Organization;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {}

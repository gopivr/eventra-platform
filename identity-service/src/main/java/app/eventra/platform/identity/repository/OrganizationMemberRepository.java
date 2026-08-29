package app.eventra.platform.identity.repository;

import app.eventra.platform.identity.domain.OrganizationMember;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationMemberRepository
    extends JpaRepository<OrganizationMember, OrganizationMember.Key> {
  boolean existsByOrganizationIdAndUserId(UUID organizationId, UUID userId);
}

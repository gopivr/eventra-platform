package app.eventra.platform.identity.repository;

import app.eventra.platform.identity.domain.UserRole;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRole.Key> {
  boolean existsByUserIdAndRoleId(UUID userId, UUID roleId);
}

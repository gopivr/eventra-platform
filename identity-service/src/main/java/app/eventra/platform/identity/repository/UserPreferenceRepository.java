package app.eventra.platform.identity.repository;

import app.eventra.platform.identity.domain.UserPreference;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, UUID> {}

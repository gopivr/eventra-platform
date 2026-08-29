package app.eventra.platform.identity.repository;

import app.eventra.platform.identity.domain.UserAccount;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
  Optional<UserAccount> findBySubject(String subject);
}

package app.eventra.platform.booking.repository;

import app.eventra.platform.booking.domain.AdmissionCredential;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdmissionCredentialRepository extends JpaRepository<AdmissionCredential, UUID> {
  Optional<AdmissionCredential> findByTokenHash(String tokenHash);
}

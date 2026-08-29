package app.eventra.platform.identity.repository;

import app.eventra.platform.identity.domain.VerificationDocumentReference;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationDocumentReferenceRepository
    extends JpaRepository<VerificationDocumentReference, UUID> {
  List<VerificationDocumentReference> findByCaseId(UUID caseId);
}

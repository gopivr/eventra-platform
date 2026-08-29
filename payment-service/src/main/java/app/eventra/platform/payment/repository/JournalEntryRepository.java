package app.eventra.platform.payment.repository;

import app.eventra.platform.payment.domain.JournalEntry;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, UUID> {
  Optional<JournalEntry> findByReferenceTypeAndReferenceId(String type, String reference);
}

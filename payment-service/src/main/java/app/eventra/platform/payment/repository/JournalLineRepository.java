package app.eventra.platform.payment.repository;

import app.eventra.platform.payment.domain.JournalLine;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalLineRepository extends JpaRepository<JournalLine, UUID> {}

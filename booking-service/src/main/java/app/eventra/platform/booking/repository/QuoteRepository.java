package app.eventra.platform.booking.repository;

import app.eventra.platform.booking.domain.Quote;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuoteRepository extends JpaRepository<Quote, UUID> {}

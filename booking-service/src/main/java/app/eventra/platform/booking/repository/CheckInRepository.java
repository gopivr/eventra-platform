package app.eventra.platform.booking.repository;

import app.eventra.platform.booking.domain.CheckIn;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckInRepository extends JpaRepository<CheckIn, UUID> {
  Optional<CheckIn> findByTicketId(UUID ticketId);
}

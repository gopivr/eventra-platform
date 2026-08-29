package app.eventra.platform.booking.repository;

import app.eventra.platform.booking.domain.SeatHold;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatHoldRepository extends JpaRepository<SeatHold, UUID> {
  List<SeatHold> findByStatusAndExpiresAtLessThanEqual(String status, Instant expiresAt);

  List<SeatHold> findByStatusAndExpiresAtAfter(String status, Instant expiresAt);
}

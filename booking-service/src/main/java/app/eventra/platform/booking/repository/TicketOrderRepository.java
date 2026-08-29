package app.eventra.platform.booking.repository;

import app.eventra.platform.booking.domain.TicketOrder;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketOrderRepository extends JpaRepository<TicketOrder, UUID> {
  Optional<TicketOrder> findByUserIdAndIdempotencyKey(UUID userId, String idempotencyKey);
}

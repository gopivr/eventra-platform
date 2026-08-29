package app.eventra.platform.booking.repository;

import app.eventra.platform.booking.domain.SeatHoldItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatHoldItemRepository extends JpaRepository<SeatHoldItem, SeatHoldItem.Key> {
  List<SeatHoldItem> findByHoldId(UUID holdId);
}

package app.eventra.platform.booking.repository;

import app.eventra.platform.booking.domain.BookingOutboxEvent;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingOutboxEventRepository extends JpaRepository<BookingOutboxEvent, UUID> {
  List<BookingOutboxEvent> findTop100ByPublishedAtIsNullOrderByOccurredAtAsc();
}

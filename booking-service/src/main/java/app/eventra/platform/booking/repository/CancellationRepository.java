package app.eventra.platform.booking.repository;

import app.eventra.platform.booking.domain.Cancellation;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CancellationRepository extends JpaRepository<Cancellation, UUID> {}

package app.eventra.platform.booking.repository;

import app.eventra.platform.booking.domain.ServiceBooking;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, UUID> {}

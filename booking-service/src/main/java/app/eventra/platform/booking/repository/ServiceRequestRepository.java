package app.eventra.platform.booking.repository;

import app.eventra.platform.booking.domain.ServiceRequest;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, UUID> {}

package app.eventra.platform.catalog.repository;

import app.eventra.platform.catalog.domain.Venue;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, UUID> {
  List<Venue> findByCityIgnoreCaseAndStatus(String city, String status);
}

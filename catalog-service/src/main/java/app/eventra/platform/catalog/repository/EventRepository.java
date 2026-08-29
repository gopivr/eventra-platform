package app.eventra.platform.catalog.repository;

import app.eventra.platform.catalog.domain.Event;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, UUID> {
  @Query(
      "select e from Event e where e.status = 'PUBLISHED' and e.visibility = 'PUBLIC' order by e.startsAt asc")
  List<Event> discover();

  @Query(
      value =
          "select e.* from eventra_catalog.event e join eventra_catalog.event_location l on l.event_id = e.id where e.status = 'PUBLISHED' and e.visibility = 'PUBLIC' and lower(l.city) like lower(concat('%', cast(:city as text), '%')) order by e.starts_at asc",
      nativeQuery = true)
  List<Event> discoverByCity(@Param("city") String city);

  List<Event> findByOrganizerOrganizationId(UUID organizationId);
}

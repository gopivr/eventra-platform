package app.eventra.platform.catalog.api;

import app.eventra.platform.catalog.api.CatalogDtos.*;
import app.eventra.platform.catalog.domain.*;
import app.eventra.platform.catalog.repository.*;
import app.eventra.platform.catalog.service.CatalogOwnership;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@Transactional
public class EventController {
  private final EventRepository events;
  private final CatalogOutboxEventRepository outbox;
  private final CatalogOwnership ownership;

  public EventController(
      EventRepository events, CatalogOutboxEventRepository outbox, CatalogOwnership ownership) {
    this.events = events;
    this.outbox = outbox;
    this.ownership = ownership;
  }

  @PostMapping("/events")
  public ResponseEntity<EventResponse> create(
      @Valid @RequestBody EventRequest request, Authentication auth) {
    ownership.requireOrganization(auth, request.organizerOrganizationId());
    Event event =
        events.save(
            new Event(
                UUID.randomUUID(),
                request.organizerOrganizationId(),
                request.title(),
                request.description(),
                request.visibility(),
                request.startsAt(),
                request.endsAt(),
                request.timezone()));
    emit(event, "EventCreatedV1");
    return ResponseEntity.created(URI.create("/v1/events/" + event.getId())).body(response(event));
  }

  @GetMapping("/events/{id}")
  public ResponseEntity<EventResponse> get(@PathVariable UUID id) {
    return events
        .findById(id)
        .filter(
            event ->
                "PUBLISHED".equals(event.getStatus()) && "PUBLIC".equals(event.getVisibility()))
        .map(event -> ResponseEntity.ok(response(event)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/events")
  public List<EventResponse> discover(@RequestParam(required = false) String city) {
    List<Event> discovered =
        city == null || city.isBlank() ? events.discover() : events.discoverByCity(city.trim());
    return discovered.stream().map(this::response).toList();
  }

  @PutMapping("/events/{id}")
  public ResponseEntity<EventResponse> update(
      @PathVariable UUID id, @Valid @RequestBody EventUpdateRequest request, Authentication auth) {
    return events
        .findById(id)
        .map(
            event -> {
              ownership.requireOrganization(auth, event.getOrganizerOrganizationId());
              event.update(
                  request.title(),
                  request.description(),
                  request.startsAt(),
                  request.endsAt(),
                  request.timezone());
              Event saved = events.save(event);
              emit(saved, "EventUpdatedV1");
              return ResponseEntity.ok(response(saved));
            })
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping("/events/{id}/publication")
  public ResponseEntity<EventResponse> publish(@PathVariable UUID id, Authentication auth) {
    return action(id, Event::publish, auth);
  }

  @PostMapping("/events/{id}/cancellations")
  public ResponseEntity<EventResponse> cancel(@PathVariable UUID id, Authentication auth) {
    return action(id, Event::cancel, auth);
  }

  private ResponseEntity<EventResponse> action(
      UUID id, java.util.function.Consumer<Event> action, Authentication auth) {
    return events
        .findById(id)
        .map(
            event -> {
              ownership.requireOrganization(auth, event.getOrganizerOrganizationId());
              action.accept(event);
              Event saved = events.save(event);
              emit(
                  saved,
                  "Event"
                      + saved.getStatus().substring(0, 1)
                      + saved.getStatus().substring(1).toLowerCase()
                      + "V1");
              return ResponseEntity.ok(response(saved));
            })
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  private void emit(Event event, String type) {
    outbox.save(
        new CatalogOutboxEvent(
            UUID.randomUUID(),
            "Event",
            event.getId().toString(),
            type,
            "{\"eventId\":\"" + event.getId() + "\"}"));
  }

  private EventResponse response(Event event) {
    return new EventResponse(
        event.getId(),
        event.getOrganizerOrganizationId(),
        event.getTitle(),
        event.getDescription(),
        event.getStatus(),
        event.getVisibility(),
        event.getStartsAt(),
        event.getEndsAt(),
        event.getTimezone(),
        event.getVersion());
  }
}

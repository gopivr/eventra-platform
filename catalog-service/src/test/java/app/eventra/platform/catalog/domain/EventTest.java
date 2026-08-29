package app.eventra.platform.catalog.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class EventTest {
  @Test
  void rejectsInvalidTimeRange() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Event(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Event",
                null,
                "PUBLIC",
                Instant.parse("2026-01-02T10:00:00Z"),
                Instant.parse("2026-01-02T09:00:00Z"),
                "Asia/Kolkata"));
  }

  @Test
  void publicationIsAnExplicitDraftAction() {
    Event event =
        new Event(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Event",
            null,
            "PUBLIC",
            Instant.parse("2026-01-02T10:00:00Z"),
            Instant.parse("2026-01-02T11:00:00Z"),
            "Asia/Kolkata");
    event.publish();
    assertEquals("PUBLISHED", event.getStatus());
    assertThrows(IllegalStateException.class, event::publish);
  }
}

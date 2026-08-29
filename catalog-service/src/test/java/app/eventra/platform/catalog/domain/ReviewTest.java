package app.eventra.platform.catalog.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ReviewTest {
  @Test
  void requiresOneTargetAndValidRating() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Review(
                UUID.randomUUID(), null, null, "booking", UUID.randomUUID(), (short) 5, "text"));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Review(
                UUID.randomUUID(),
                UUID.randomUUID(),
                null,
                "booking",
                UUID.randomUUID(),
                (short) 6,
                "text"));
  }
}

package app.eventra.platform.common.storage;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class ObjectStorageUrlSignerTest {
  @Test
  void signsScopedUrlsAndRejectsTraversal() {
    var signer =
        new ObjectStorageUrlSigner(
            "https://objects.example/bucket", "0123456789abcdef", Duration.ofMinutes(5));
    var signed = signer.signPut("events/id/photo one.png", "image/png");
    assertEquals("PUT", signed.method());
    assertTrue(signed.url().toString().contains("photo%20one.png"));
    assertTrue(signed.url().toString().contains("signature="));
    assertThrows(IllegalArgumentException.class, () -> signer.signGet("../secret"));
  }
}

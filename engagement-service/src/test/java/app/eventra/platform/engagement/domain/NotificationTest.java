package app.eventra.platform.engagement.domain;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class NotificationTest {
  @Test
  void readTransitionIsIdempotent() {
    Notification notification =
        new Notification(UUID.randomUUID(), UUID.randomUUID(), "TYPE", "Title", "Body", null, null);
    notification.markRead();
    assertTrue(notification.isRead());
    notification.markRead();
    assertTrue(notification.isRead());
  }
}

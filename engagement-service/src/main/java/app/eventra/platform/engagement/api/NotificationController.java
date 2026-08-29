package app.eventra.platform.engagement.api;

import app.eventra.platform.engagement.service.NotificationService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/notifications")
public class NotificationController {
  private final NotificationService service;

  public NotificationController(NotificationService service) {
    this.service = service;
  }

  public record Response(UUID id, String title, String body, boolean read) {}

  @GetMapping
  public List<Response> list(Authentication a) {
    UUID user = UUID.nameUUIDFromBytes(a.getName().getBytes());
    return service.list(user).stream()
        .map(n -> new Response(n.getId(), n.getTitle(), n.getBody(), n.isRead()))
        .toList();
  }

  @PostMapping("/{id}/read")
  public ResponseEntity<Void> read(@PathVariable UUID id, Authentication a) {
    service.markRead(id, UUID.nameUUIDFromBytes(a.getName().getBytes()));
    return ResponseEntity.noContent().build();
  }
}

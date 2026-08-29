package app.eventra.platform.engagement.api;

import app.eventra.platform.engagement.domain.*;
import app.eventra.platform.engagement.repository.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
public class EngagementController {
  private final SavedItemRepository saved;
  private final SupportCaseRepository cases;
  private final DisputeRepository disputes;
  private final SupportCaseMessageRepository messages;
  private final DisputeEvidenceRepository evidence;

  public EngagementController(
      SavedItemRepository saved,
      SupportCaseRepository cases,
      DisputeRepository disputes,
      SupportCaseMessageRepository messages,
      DisputeEvidenceRepository evidence) {
    this.saved = saved;
    this.cases = cases;
    this.disputes = disputes;
    this.messages = messages;
    this.evidence = evidence;
  }

  public record SaveRequest(@NotBlank String itemType, @NotBlank String itemId) {}

  public record CaseRequest(@NotBlank String subject) {}

  public record DisputeRequest(
      @NotBlank String bookingId, @NotBlank String paymentId, @NotBlank String reason) {}

  public record MessageRequest(@NotBlank @Size(max = 10000) String body) {}

  public record EvidenceRequest(
      @NotBlank @Size(max = 500) String objectStorageKey,
      @NotBlank @Pattern(regexp = "application/pdf|image/(png|jpeg)") String contentType) {}

  @PostMapping("/saved-items")
  public ResponseEntity<Void> save(@Valid @RequestBody SaveRequest r, Authentication a) {
    saved.save(new SavedItem(user(a), r.itemType(), r.itemId()));
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/saved-items/{itemType}/{itemId}")
  public ResponseEntity<Void> unsave(
      @PathVariable String itemType, @PathVariable String itemId, Authentication a) {
    saved.deleteById(new SavedItem.Key(user(a), itemType, itemId));
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/saved-items")
  public List<SavedItem> listSaved(Authentication a) {
    return saved.findByUserId(user(a));
  }

  @PostMapping("/support-cases")
  public ResponseEntity<UUID> createCase(@Valid @RequestBody CaseRequest r, Authentication a) {
    SupportCase c = cases.save(new SupportCase(UUID.randomUUID(), user(a), r.subject()));
    return ResponseEntity.created(URI.create("/v1/support-cases/" + c.getId())).body(c.getId());
  }

  @GetMapping("/support-cases")
  public List<SupportCase> listCases(Authentication a) {
    return cases.findByRequesterUserId(user(a));
  }

  @PostMapping("/support-cases/{id}/messages")
  public ResponseEntity<UUID> message(
      @PathVariable UUID id, @Valid @RequestBody MessageRequest r, Authentication a) {
    requireCaseOwner(id, user(a));
    SupportCaseMessage message =
        messages.save(new SupportCaseMessage(UUID.randomUUID(), id, user(a), r.body()));
    return ResponseEntity.created(
            URI.create("/v1/support-cases/" + id + "/messages/" + message.getId()))
        .body(message.getId());
  }

  @GetMapping("/support-cases/{id}/messages")
  public List<SupportCaseMessage> messages(@PathVariable UUID id, Authentication a) {
    requireCaseOwner(id, user(a));
    return messages.findByCaseIdOrderByCreatedAtAsc(id);
  }

  @PostMapping("/disputes")
  public ResponseEntity<UUID> createDispute(
      @Valid @RequestBody DisputeRequest r, Authentication a) {
    Dispute d =
        disputes.save(
            new Dispute(UUID.randomUUID(), user(a), r.bookingId(), r.paymentId(), r.reason()));
    return ResponseEntity.created(URI.create("/v1/disputes/" + d.getId())).body(d.getId());
  }

  @GetMapping("/disputes")
  public List<Dispute> listDisputes(Authentication a) {
    return disputes.findByOpenedByUserId(user(a));
  }

  @PostMapping("/disputes/{id}/evidence")
  public ResponseEntity<UUID> evidence(
      @PathVariable UUID id, @Valid @RequestBody EvidenceRequest r, Authentication a) {
    requireDisputeOwner(id, user(a));
    DisputeEvidence item =
        evidence.save(
            new DisputeEvidence(UUID.randomUUID(), id, r.objectStorageKey(), r.contentType()));
    return ResponseEntity.created(URI.create("/v1/disputes/" + id + "/evidence/" + item.getId()))
        .body(item.getId());
  }

  @GetMapping("/disputes/{id}/evidence")
  public List<DisputeEvidence> evidence(@PathVariable UUID id, Authentication a) {
    requireDisputeOwner(id, user(a));
    return evidence.findByDisputeIdOrderByCreatedAtAsc(id);
  }

  private UUID user(Authentication a) {
    return UUID.nameUUIDFromBytes(a.getName().getBytes(StandardCharsets.UTF_8));
  }

  private void requireCaseOwner(UUID id, UUID user) {
    if (!cases.findById(id).map(c -> c.getRequesterUserId().equals(user)).orElse(false))
      throw new AccessDeniedException("support case is owned by another user");
  }

  private void requireDisputeOwner(UUID id, UUID user) {
    if (!disputes.findById(id).map(d -> d.getOpenedByUserId().equals(user)).orElse(false))
      throw new AccessDeniedException("dispute is owned by another user");
  }
}

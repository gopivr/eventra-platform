package app.eventra.platform.payment.api;

import app.eventra.platform.payment.domain.*;
import app.eventra.platform.payment.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
public class PaymentController {
  private final PaymentService service;

  public PaymentController(PaymentService service) {
    this.service = service;
  }

  public record QuoteRequest(@NotEmpty List<@PositiveOrZero Long> lineAmounts) {}

  public record QuoteResponse(long totalMinor, String currency) {}

  public record IntentRequest(
      @NotBlank String orderReference,
      @Positive long amountMinor,
      @Pattern(regexp = "[A-Z]{3}") String currency) {}

  public record IntentResponse(UUID id, long amountMinor, String currency, String status) {}

  public record RefundRequest(
      @NotNull UUID paymentIntentId,
      @Positive long amountMinor,
      @Pattern(regexp = "[A-Z]{3}") String currency) {}

  public record JournalLineRequest(
      @NotNull UUID ledgerAccountId,
      @PositiveOrZero long debitMinor,
      @PositiveOrZero long creditMinor,
      @Pattern(regexp = "[A-Z]{3}") String currency) {}

  public record JournalRequest(
      @NotBlank String referenceType,
      @NotBlank String referenceId,
      @NotEmpty List<@Valid JournalLineRequest> lines) {}

  public record SettlementItemRequest(@NotNull UUID journalEntryId, @Positive long amountMinor) {}

  public record SettlementRequest(
      @NotBlank String beneficiaryReference,
      @Pattern(regexp = "[A-Z]{3}") String currency,
      @NotEmpty List<@Valid SettlementItemRequest> items) {}

  public record SettlementResponse(UUID id, String status, long amountMinor, String currency) {}

  public record PayoutResponse(UUID id, String status, String providerReference) {}

  @PostMapping("/pricing/quotes")
  public QuoteResponse quote(@Valid @RequestBody QuoteRequest r) {
    return new QuoteResponse(service.quote(r.lineAmounts()), "INR");
  }

  @PostMapping("/payment-intents")
  public ResponseEntity<IntentResponse> intent(
      @RequestHeader("Idempotency-Key") String key,
      @Valid @RequestBody IntentRequest r,
      Authentication a) {
    PaymentIntent p =
        service.intent(a.getName(), r.orderReference(), key, r.amountMinor(), r.currency());
    return ResponseEntity.ok(intentResponse(p));
  }

  @PostMapping("/payment-intents/{id}/capture")
  public IntentResponse capture(@PathVariable UUID id) {
    return intentResponse(service.capture(id));
  }

  @PostMapping("/refunds")
  public ResponseEntity<UUID> refund(
      @RequestHeader("Idempotency-Key") String key, @Valid @RequestBody RefundRequest r) {
    return ResponseEntity.accepted()
        .body(service.refund(r.paymentIntentId(), key, r.amountMinor(), r.currency()).getId());
  }

  @PostMapping("/payments/webhooks/{provider}")
  public ResponseEntity<Void> webhook(
      @PathVariable String provider,
      @RequestHeader("X-Provider-Event-Id") String eventId,
      @RequestHeader("X-Webhook-Signature") String signature,
      @RequestHeader("X-Webhook-Timestamp") long timestamp,
      @RequestBody String payload) {
    service.webhook(provider, eventId, payload, signature, timestamp);
    return ResponseEntity.accepted().build();
  }

  @PostMapping("/ledger/journal-entries")
  public ResponseEntity<UUID> journal(@Valid @RequestBody JournalRequest r) {
    List<JournalLine> lines =
        r.lines().stream()
            .map(
                line ->
                    new JournalLine(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        line.ledgerAccountId(),
                        line.debitMinor(),
                        line.creditMinor(),
                        line.currency()))
            .toList();
    return ResponseEntity.ok(service.post(r.referenceType(), r.referenceId(), lines).getId());
  }

  @PostMapping("/settlements")
  public SettlementResponse settlement(@Valid @RequestBody SettlementRequest r) {
    Settlement s =
        service.settle(
            r.beneficiaryReference(),
            r.currency(),
            r.items().stream()
                .map(i -> new PaymentService.SettlementItem(i.journalEntryId(), i.amountMinor()))
                .toList());
    return new SettlementResponse(s.getId(), s.getStatus(), s.getAmountMinor(), s.getCurrency());
  }

  @PostMapping("/settlements/{id}/payouts")
  public PayoutResponse payout(
      @PathVariable UUID id, @RequestHeader("Idempotency-Key") String key) {
    Payout p = service.payout(id, key);
    return new PayoutResponse(p.getId(), p.getStatus(), p.getProviderReference());
  }

  @PostMapping("/internal/reconciliation-runs")
  public ResponseEntity<UUID> reconcile() {
    return ResponseEntity.accepted().body(service.reconcile().getId());
  }

  private IntentResponse intentResponse(PaymentIntent p) {
    return new IntentResponse(p.getId(), p.getAmountMinor(), p.getCurrency(), p.getStatus());
  }
}

package app.eventra.platform.payment.service;

import app.eventra.platform.payment.domain.*;
import app.eventra.platform.payment.provider.*;
import app.eventra.platform.payment.repository.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
  private final PaymentIntentRepository intents;
  private final PaymentAttemptRepository attempts;
  private final JournalEntryRepository entries;
  private final JournalLineRepository lines;
  private final RefundRepository refunds;
  private final WebhookEventRepository webhooks;
  private final PaymentOutboxEventRepository outbox;
  private final SettlementRepository settlements;
  private final SettlementLineRepository settlementLines;
  private final PayoutRepository payouts;
  private final ReconciliationRunRepository reconciliationRuns;
  private final PaymentProviderRegistry providers;
  private final WebhookSignatureVerifier signatures;

  public PaymentService(
      PaymentIntentRepository intents,
      PaymentAttemptRepository attempts,
      JournalEntryRepository entries,
      JournalLineRepository lines,
      RefundRepository refunds,
      WebhookEventRepository webhooks,
      PaymentOutboxEventRepository outbox,
      SettlementRepository settlements,
      SettlementLineRepository settlementLines,
      PayoutRepository payouts,
      ReconciliationRunRepository reconciliationRuns,
      PaymentProviderRegistry providers,
      WebhookSignatureVerifier signatures) {
    this.intents = intents;
    this.attempts = attempts;
    this.entries = entries;
    this.lines = lines;
    this.refunds = refunds;
    this.webhooks = webhooks;
    this.outbox = outbox;
    this.settlements = settlements;
    this.settlementLines = settlementLines;
    this.payouts = payouts;
    this.reconciliationRuns = reconciliationRuns;
    this.providers = providers;
    this.signatures = signatures;
  }

  public long quote(List<Long> amounts) {
    if (amounts == null || amounts.isEmpty() || amounts.stream().anyMatch(a -> a == null || a < 0))
      throw new IllegalArgumentException("invalid line amounts");
    return amounts.stream().mapToLong(Long::longValue).sum();
  }

  @Transactional
  public PaymentIntent intent(
      String payer, String order, String key, long amount, String currency) {
    return intents
        .findByPayerReferenceAndIdempotencyKey(payer, key)
        .orElseGet(
            () ->
                intents.save(
                    new PaymentIntent(UUID.randomUUID(), payer, order, amount, currency, key)));
  }

  @Transactional
  public PaymentIntent capture(UUID id) {
    PaymentIntent payment = intents.findById(id).orElseThrow();
    if ("CAPTURED".equals(payment.getStatus())) return payment;
    PaymentProviderAdapter provider = providers.active();
    PaymentProviderAdapter.CaptureResult result = provider.capture(payment);
    attempts.save(
        new PaymentAttempt(
            UUID.randomUUID(),
            id,
            provider.name(),
            result.providerReference(),
            result.captured() ? "CAPTURED" : "FAILED",
            payment.getAmountMinor()));
    if (!result.captured()) throw new IllegalStateException("provider declined capture");
    payment.capture();
    PaymentIntent saved = intents.save(payment);
    UUID messageId = UUID.randomUUID();
    UUID userId =
        UUID.nameUUIDFromBytes(
            payment.getPayerReference().getBytes(java.nio.charset.StandardCharsets.UTF_8));
    outbox.save(
        new PaymentOutboxEvent(
            messageId,
            id.toString(),
            "{\"messageId\":\""
                + messageId
                + "\",\"paymentIntentId\":\""
                + id
                + "\",\"userId\":\""
                + userId
                + "\",\"orderId\":\""
                + payment.getOrderReference()
                + "\",\"amountMinor\":"
                + payment.getAmountMinor()
                + ",\"currency\":\""
                + payment.getCurrency()
                + "\",\"occurredAt\":\""
                + Instant.now()
                + "\"}"));
    return saved;
  }

  @Transactional
  public Refund refund(UUID paymentId, String key, long amount, String currency) {
    PaymentIntent payment = intents.findById(paymentId).orElseThrow();
    if (!"CAPTURED".equals(payment.getStatus()))
      throw new IllegalStateException("only captured payments can be refunded");
    if (!payment.getCurrency().equals(currency) || amount > payment.getAmountMinor())
      throw new IllegalArgumentException("refund exceeds the captured payment");
    return refunds
        .findByPaymentIntentIdAndIdempotencyKey(paymentId, key)
        .orElseGet(
            () -> refunds.save(new Refund(UUID.randomUUID(), paymentId, key, amount, currency)));
  }

  @Transactional
  public boolean webhook(
      String provider, String eventId, String payload, String signature, long timestamp) {
    signatures.verify(provider, payload, signature, timestamp);
    if (webhooks.existsByProviderAndProviderEventId(provider, eventId)) return false;
    webhooks.save(new WebhookEvent(UUID.randomUUID(), provider, eventId, payload));
    return true;
  }

  @Transactional
  public JournalEntry post(String type, String reference, List<JournalLine> requestedLines) {
    return entries
        .findByReferenceTypeAndReferenceId(type, reference)
        .orElseGet(
            () -> {
              long debits = requestedLines.stream().mapToLong(JournalLine::getDebitMinor).sum();
              long credits = requestedLines.stream().mapToLong(JournalLine::getCreditMinor).sum();
              if (requestedLines.isEmpty() || debits != credits)
                throw new IllegalArgumentException("journal entry must balance");
              String currency = requestedLines.getFirst().getCurrency();
              if (requestedLines.stream().anyMatch(line -> !currency.equals(line.getCurrency())))
                throw new IllegalArgumentException("journal entry currencies must match");
              JournalEntry entry =
                  entries.save(new JournalEntry(UUID.randomUUID(), type, reference));
              requestedLines.stream()
                  .map(line -> line.forEntry(entry.getId()))
                  .forEach(lines::save);
              return entry;
            });
  }

  @Transactional
  public Settlement settle(String beneficiary, String currency, List<SettlementItem> items) {
    if (items == null || items.isEmpty())
      throw new IllegalArgumentException("settlement requires journal entries");
    long amount = items.stream().mapToLong(SettlementItem::amountMinor).sum();
    Settlement settlement =
        settlements.save(new Settlement(UUID.randomUUID(), beneficiary, currency, amount));
    items.forEach(
        item -> {
          if (!entries.existsById(item.journalEntryId()))
            throw new IllegalArgumentException("journal entry does not exist");
          settlementLines.save(
              new SettlementLine(
                  UUID.randomUUID(),
                  settlement.getId(),
                  item.journalEntryId(),
                  item.amountMinor()));
        });
    return settlement;
  }

  @Transactional
  public Payout payout(UUID settlementId, String key) {
    Settlement settlement = settlements.findById(settlementId).orElseThrow();
    return payouts
        .findByBeneficiaryReferenceAndIdempotencyKey(settlement.getBeneficiaryReference(), key)
        .orElseGet(
            () -> {
              settlement.markPaying();
              Payout payout =
                  new Payout(
                      UUID.randomUUID(), settlementId, settlement.getBeneficiaryReference(), key);
              PaymentProviderAdapter.PayoutResult result =
                  providers
                      .active()
                      .payout(payout, settlement.getAmountMinor(), settlement.getCurrency());
              payout.submit(result.providerReference(), result.feeMinor());
              settlements.save(settlement);
              return payouts.save(payout);
            });
  }

  @Transactional
  public ReconciliationRun reconcile() {
    PaymentProviderAdapter provider = providers.active();
    ReconciliationRun run =
        reconciliationRuns.save(new ReconciliationRun(UUID.randomUUID(), provider.name()));
    try {
      List<PaymentProviderAdapter.ProviderTransaction> transactions =
          provider.transactionsSince(Instant.now().minusSeconds(86400));
      int completed = 0;
      for (var transaction : transactions) {
        var payout = payouts.findByProviderReference(transaction.providerReference());
        if (payout.isPresent()) {
          if ("COMPLETED".equals(transaction.status())) {
            payout.get().complete();
            settlements.findById(payout.get().getSettlementId()).ifPresent(Settlement::paid);
            completed++;
          } else if ("FAILED".equals(transaction.status())) payout.get().fail();
          payouts.save(payout.get());
        }
      }
      run.complete(
          "{\"transactions\":" + transactions.size() + ",\"completedPayouts\":" + completed + "}");
      return reconciliationRuns.save(run);
    } catch (RuntimeException failure) {
      run.fail("{\"error\":\"provider reconciliation failed\"}");
      reconciliationRuns.save(run);
      throw failure;
    }
  }

  public record SettlementItem(UUID journalEntryId, long amountMinor) {
    public SettlementItem {
      if (journalEntryId == null || amountMinor <= 0)
        throw new IllegalArgumentException("invalid settlement line");
    }
  }
}

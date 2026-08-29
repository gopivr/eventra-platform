package app.eventra.platform.payment.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class PaymentDomainTest {
  @Test
  void intentRejectsNonPositiveAmount() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new PaymentIntent(UUID.randomUUID(), "payer", "order", 0, "INR", "key"));
  }

  @Test
  void journalLineCannotBeBothDebitAndCredit() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new JournalLine(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 10, 10, "INR"));
  }

  @Test
  void settlementRejectsZeroAmount() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Settlement(UUID.randomUUID(), "vendor", "INR", 0));
  }

  @Test
  void payoutRejectsNegativeProviderFee() {
    Payout payout = new Payout(UUID.randomUUID(), UUID.randomUUID(), "vendor", "key");
    assertThrows(IllegalArgumentException.class, () -> payout.submit("provider", -1));
  }
}

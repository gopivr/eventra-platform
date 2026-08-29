package app.eventra.platform.payment.provider;

import app.eventra.platform.payment.domain.PaymentIntent;
import app.eventra.platform.payment.domain.Payout;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class SandboxPaymentProviderAdapter implements PaymentProviderAdapter {
  public String name() {
    return "sandbox";
  }

  public CaptureResult capture(PaymentIntent intent) {
    return new CaptureResult("pay_" + UUID.randomUUID(), true);
  }

  public PayoutResult payout(Payout payout, long amount, String currency) {
    return new PayoutResult("po_" + UUID.randomUUID(), 0);
  }

  public List<ProviderTransaction> transactionsSince(Instant since) {
    return List.of();
  }
}

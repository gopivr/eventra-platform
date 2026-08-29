package app.eventra.platform.payment.provider;

import app.eventra.platform.payment.domain.PaymentIntent;
import app.eventra.platform.payment.domain.Payout;
import java.time.Instant;
import java.util.List;

public interface PaymentProviderAdapter {
  String name();

  CaptureResult capture(PaymentIntent intent);

  PayoutResult payout(Payout payout, long amountMinor, String currency);

  List<ProviderTransaction> transactionsSince(Instant since);

  record CaptureResult(String providerReference, boolean captured) {}

  record PayoutResult(String providerReference, long feeMinor) {}

  record ProviderTransaction(String providerReference, String status) {}
}

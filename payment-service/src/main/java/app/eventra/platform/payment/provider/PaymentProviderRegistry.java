package app.eventra.platform.payment.provider;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentProviderRegistry {
  private final List<PaymentProviderAdapter> adapters;
  private final String configured;

  public PaymentProviderRegistry(
      List<PaymentProviderAdapter> adapters,
      @Value("${payment.provider:sandbox}") String configured) {
    this.adapters = adapters;
    this.configured = configured;
  }

  public PaymentProviderAdapter active() {
    return adapters.stream()
        .filter(a -> a.name().equalsIgnoreCase(configured))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("payment provider is not configured: " + configured));
  }
}

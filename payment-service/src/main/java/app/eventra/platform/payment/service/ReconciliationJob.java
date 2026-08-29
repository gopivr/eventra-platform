package app.eventra.platform.payment.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReconciliationJob {
  private final PaymentService payments;

  public ReconciliationJob(PaymentService payments) {
    this.payments = payments;
  }

  @Scheduled(fixedDelayString = "${payment.reconciliation-delay:3600000}")
  public void run() {
    payments.reconcile();
  }
}

package app.eventra.platform.booking.service;

import java.time.Instant;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SeatHoldReconciliationJob {
  private final BookingService booking;

  public SeatHoldReconciliationJob(BookingService booking) {
    this.booking = booking;
  }

  @Scheduled(fixedDelayString = "${booking.hold-reconciliation-delay:30000}")
  public void reconcile() {
    booking.reconcileHolds(Instant.now());
  }
}

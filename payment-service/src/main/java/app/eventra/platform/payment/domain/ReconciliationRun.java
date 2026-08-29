package app.eventra.platform.payment.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reconciliation_run", schema = "eventra_payment")
public class ReconciliationRun {
  @Id private UUID id;

  @Column(nullable = false)
  private String provider;

  @Column(name = "started_at", nullable = false)
  private Instant startedAt;

  @Column(name = "completed_at")
  private Instant completedAt;

  @Column(nullable = false)
  private String status = "RUNNING";

  @Column(columnDefinition = "jsonb")
  private String result;

  protected ReconciliationRun() {}

  public ReconciliationRun(UUID id, String provider) {
    this.id = id;
    this.provider = provider;
    startedAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public String getStatus() {
    return status;
  }

  public void complete(String result) {
    this.result = result;
    completedAt = Instant.now();
    status = "COMPLETED";
  }

  public void fail(String result) {
    this.result = result;
    completedAt = Instant.now();
    status = "FAILED";
  }
}

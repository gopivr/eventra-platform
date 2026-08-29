package app.eventra.platform.payment.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "settlement_line", schema = "eventra_payment")
public class SettlementLine {
  @Id private UUID id;

  @Column(name = "settlement_id", nullable = false)
  private UUID settlementId;

  @Column(name = "journal_entry_id", nullable = false)
  private UUID journalEntryId;

  @Column(name = "amount_minor", nullable = false)
  private long amountMinor;

  protected SettlementLine() {}

  public SettlementLine(UUID id, UUID settlement, UUID journal, long amount) {
    if (amount <= 0) throw new IllegalArgumentException("settlement line amount must be positive");
    this.id = id;
    settlementId = settlement;
    journalEntryId = journal;
    amountMinor = amount;
  }
}

package app.eventra.platform.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "journal_line", schema = "eventra_payment")
public class JournalLine {
  @Id private UUID id;

  @Column(name = "journal_entry_id", nullable = false)
  private UUID journalEntryId;

  @Column(name = "ledger_account_id", nullable = false)
  private UUID ledgerAccountId;

  @Column(name = "debit_minor", nullable = false)
  private long debitMinor;

  @Column(name = "credit_minor", nullable = false)
  private long creditMinor;

  @Column(nullable = false)
  private String currency;

  protected JournalLine() {}

  public JournalLine(UUID id, UUID entry, UUID account, long debit, long credit, String currency) {
    if ((debit > 0) == (credit > 0))
      throw new IllegalArgumentException("line must be debit or credit");
    this.id = id;
    journalEntryId = entry;
    ledgerAccountId = account;
    debitMinor = debit;
    creditMinor = credit;
    this.currency = currency;
  }

  public UUID getJournalEntryId() {
    return journalEntryId;
  }

  public UUID getLedgerAccountId() {
    return ledgerAccountId;
  }

  public long getDebitMinor() {
    return debitMinor;
  }

  public long getCreditMinor() {
    return creditMinor;
  }

  public String getCurrency() {
    return currency;
  }

  public JournalLine forEntry(UUID entry) {
    return new JournalLine(
        UUID.randomUUID(), entry, ledgerAccountId, debitMinor, creditMinor, currency);
  }
}

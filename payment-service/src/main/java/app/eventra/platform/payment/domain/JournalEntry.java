package app.eventra.platform.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "journal_entry", schema = "eventra_payment")
public class JournalEntry {
  @Id private UUID id;

  @Column(name = "reference_type", nullable = false)
  private String referenceType;

  @Column(name = "reference_id", nullable = false)
  private String referenceId;

  @Column(nullable = false)
  private String status = "POSTED";

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "posted_at", nullable = false)
  private Instant postedAt;

  protected JournalEntry() {}

  public JournalEntry(UUID id, String type, String reference) {
    this.id = id;
    referenceType = type;
    referenceId = reference;
    createdAt = Instant.now();
    postedAt = createdAt;
  }

  public UUID getId() {
    return id;
  }
}

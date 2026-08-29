package app.eventra.platform.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "admission_credential", schema = "eventra_booking")
public class AdmissionCredential {
  @Id
  @Column(name = "ticket_id")
  private UUID ticketId;

  @Column(name = "token_hash", nullable = false, unique = true, length = 64)
  private String tokenHash;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected AdmissionCredential() {}

  public AdmissionCredential(UUID ticket, String hash) {
    ticketId = ticket;
    tokenHash = hash;
    createdAt = Instant.now();
  }

  public UUID getTicketId() {
    return ticketId;
  }

  public String getTokenHash() {
    return tokenHash;
  }
}

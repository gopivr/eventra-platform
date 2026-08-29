package app.eventra.platform.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "check_in", schema = "eventra_booking")
public class CheckIn {
  @Id private UUID id;

  @Column(name = "ticket_id", nullable = false, unique = true)
  private UUID ticketId;

  @Column(name = "event_id", nullable = false)
  private UUID eventId;

  @Column(name = "scanner_subject", nullable = false)
  private String scannerSubject;

  @Column(name = "checked_in_at", nullable = false)
  private Instant checkedInAt;

  protected CheckIn() {}

  public CheckIn(UUID id, UUID ticket, UUID event, String scanner) {
    this.id = id;
    ticketId = ticket;
    eventId = event;
    scannerSubject = scanner;
    checkedInAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public UUID getTicketId() {
    return ticketId;
  }
}

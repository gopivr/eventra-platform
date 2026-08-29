package app.eventra.platform.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "service_booking", schema = "eventra_booking")
public class ServiceBooking {
  @Id private UUID id;

  @Column(name = "request_id", nullable = false)
  private UUID requestId;

  @Column(name = "quote_id", nullable = false)
  private UUID quoteId;

  @Column(nullable = false)
  private String state = "PENDING_PAYMENT";

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected ServiceBooking() {}

  public ServiceBooking(UUID id, UUID request, UUID quote) {
    this.id = id;
    requestId = request;
    quoteId = quote;
    createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public String getState() {
    return state;
  }

  public void accept() {
    state = "CONFIRMED";
  }

  public void decline() {
    state = "CANCELLED";
  }
}

package app.eventra.platform.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "provider_availability", schema = "eventra_booking")
public class ProviderAvailability {
  @Id private UUID id;

  @Column(name = "provider_id", nullable = false)
  private UUID providerId;

  @Column(name = "starts_at", nullable = false)
  private Instant startsAt;

  @Column(name = "ends_at", nullable = false)
  private Instant endsAt;

  @Column(nullable = false)
  private boolean available;

  protected ProviderAvailability() {}

  public ProviderAvailability(UUID id, UUID provider, Instant starts, Instant ends) {
    if (!ends.isAfter(starts)) throw new IllegalArgumentException("endsAt must be after startsAt");
    this.id = id;
    providerId = provider;
    startsAt = starts;
    endsAt = ends;
    available = true;
  }

  public UUID getId() {
    return id;
  }
}

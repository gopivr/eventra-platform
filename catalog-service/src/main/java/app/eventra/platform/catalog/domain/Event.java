package app.eventra.platform.catalog.domain;

import jakarta.persistence.*;
import java.time.*;
import java.util.UUID;

@Entity
@Table(name = "event", schema = "eventra_catalog")
public class Event {
  @Id private UUID id;

  @Column(name = "organizer_organization_id", nullable = false)
  private UUID organizerOrganizationId;

  @Column(nullable = false)
  private String title;

  private String description;

  @Column(nullable = false)
  private String status = "DRAFT";

  @Column(nullable = false)
  private String visibility;

  @Column(name = "starts_at", nullable = false)
  private Instant startsAt;

  @Column(name = "ends_at", nullable = false)
  private Instant endsAt;

  @Column(nullable = false)
  private String timezone;

  @Column(name = "location_id")
  private UUID locationId;

  @Version private long version;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected Event() {}

  public Event(
      UUID id,
      UUID organizationId,
      String title,
      String description,
      String visibility,
      Instant startsAt,
      Instant endsAt,
      String timezone) {
    if (!endsAt.isAfter(startsAt))
      throw new IllegalArgumentException("endsAt must be after startsAt");
    this.id = id;
    this.organizerOrganizationId = organizationId;
    this.title = title;
    this.description = description;
    this.visibility = visibility;
    this.startsAt = startsAt;
    this.endsAt = endsAt;
    this.timezone = ZoneId.of(timezone).getId();
    this.createdAt = Instant.now();
    this.updatedAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getOrganizerOrganizationId() {
    return organizerOrganizationId;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getStatus() {
    return status;
  }

  public String getVisibility() {
    return visibility;
  }

  public Instant getStartsAt() {
    return startsAt;
  }

  public Instant getEndsAt() {
    return endsAt;
  }

  public String getTimezone() {
    return timezone;
  }

  public long getVersion() {
    return version;
  }

  public void update(
      String title, String description, Instant startsAt, Instant endsAt, String timezone) {
    if (!endsAt.isAfter(startsAt))
      throw new IllegalArgumentException("endsAt must be after startsAt");
    this.title = title;
    this.description = description;
    this.startsAt = startsAt;
    this.endsAt = endsAt;
    this.timezone = ZoneId.of(timezone).getId();
    this.updatedAt = Instant.now();
  }

  public void publish() {
    if (!"DRAFT".equals(status))
      throw new IllegalStateException("Only draft events can be published");
    status = "PUBLISHED";
    updatedAt = Instant.now();
  }

  public void cancel() {
    if ("COMPLETED".equals(status))
      throw new IllegalStateException("Completed events cannot be cancelled");
    status = "CANCELLED";
    updatedAt = Instant.now();
  }
}

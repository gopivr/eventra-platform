package app.eventra.platform.identity.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_preference", schema = "eventra_identity")
public class UserPreference {
  @Id
  @Column(name = "user_id")
  private UUID userId;

  @Column(nullable = false)
  private String locale = "en-IN";

  @Column(nullable = false)
  private String timezone = "UTC";

  @Column(name = "marketing_email", nullable = false)
  private boolean marketingEmail;

  @Column(name = "marketing_sms", nullable = false)
  private boolean marketingSms;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected UserPreference() {}

  public UserPreference(UUID userId) {
    this.userId = userId;
    this.updatedAt = Instant.now();
  }

  public UUID getUserId() {
    return userId;
  }

  public String getLocale() {
    return locale;
  }

  public String getTimezone() {
    return timezone;
  }

  public boolean isMarketingEmail() {
    return marketingEmail;
  }

  public boolean isMarketingSms() {
    return marketingSms;
  }

  public void update(String locale, String timezone, boolean email, boolean sms) {
    this.locale = locale;
    this.timezone = timezone;
    this.marketingEmail = email;
    this.marketingSms = sms;
    this.updatedAt = Instant.now();
  }
}

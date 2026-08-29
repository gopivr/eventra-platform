package app.eventra.platform.identity.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_account", schema = "eventra_identity")
public class UserAccount {
  @Id private UUID id;

  @Column(nullable = false, unique = true)
  private String subject;

  @Column(name = "email_display", nullable = false)
  private String emailDisplay;

  @Column(name = "email_normalized", nullable = false, unique = true)
  private String emailNormalized;

  @Column(name = "phone_e164")
  private String phoneE164;

  @Column(nullable = false)
  private String status = "ACTIVE";

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Version private long version;

  protected UserAccount() {}

  public UserAccount(UUID id, String subject, String emailDisplay) {
    this.id = id;
    this.subject = subject;
    this.emailDisplay = emailDisplay;
    this.emailNormalized = emailDisplay.trim().toLowerCase(java.util.Locale.ROOT);
    this.createdAt = Instant.now();
    this.updatedAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public String getSubject() {
    return subject;
  }

  public String getEmailDisplay() {
    return emailDisplay;
  }

  public void updateEmail(String email) {
    this.emailDisplay = email;
    this.emailNormalized = email.trim().toLowerCase(java.util.Locale.ROOT);
    this.updatedAt = Instant.now();
  }
}

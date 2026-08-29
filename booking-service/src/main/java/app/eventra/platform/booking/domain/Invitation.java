package app.eventra.platform.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "invitation", schema = "eventra_booking")
public class Invitation {
  @Id private UUID id;

  @Column(name = "event_id", nullable = false)
  private UUID eventId;

  @Column(name = "inviter_user_id", nullable = false)
  private UUID inviterUserId;

  @Column(name = "invitee_email", nullable = false)
  private String inviteeEmail;

  @Column(name = "token_hash", nullable = false, unique = true, length = 64)
  private String tokenHash;

  @Column(nullable = false)
  private String status = "PENDING";

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected Invitation() {}

  public Invitation(UUID id, UUID event, UUID inviter, String email, String hash, Instant expires) {
    this.id = id;
    eventId = event;
    inviterUserId = inviter;
    inviteeEmail = email;
    tokenHash = hash;
    expiresAt = expires;
    createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public String getStatus() {
    return status;
  }

  public void respond(String response) {
    if (Instant.now().isAfter(expiresAt)) throw new IllegalStateException("invitation expired");
    if (!Set.of("ACCEPTED", "DECLINED").contains(response))
      throw new IllegalArgumentException("invalid response");
    status = response;
  }
}

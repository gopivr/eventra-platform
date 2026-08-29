package app.eventra.platform.identity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "role", schema = "eventra_identity")
public class Role {
  @Id private UUID id;

  @Column(nullable = false, unique = true)
  private String code;

  @Column(name = "display_name", nullable = false)
  private String displayName;

  protected Role() {}

  public UUID getId() {
    return id;
  }

  public String getCode() {
    return code;
  }
}

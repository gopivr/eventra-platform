package app.eventra.platform.identity.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserAccountTest {
  @Test
  void emailDisplayIsRetainedWhileUniquenessValueIsNormalized() {
    UserAccount account =
        new UserAccount(UUID.randomUUID(), "oidc-subject", " Person@Example.COM ");
    assertEquals(" Person@Example.COM ", account.getEmailDisplay());
    account.updateEmail(" New@Example.COM ");
    assertEquals(" New@Example.COM ", account.getEmailDisplay());
  }
}

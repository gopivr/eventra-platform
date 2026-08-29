package app.eventra.platform.catalog.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

class CatalogOwnershipTest {
  @Test
  void requiresOrganizationClaim() {
    UUID organization = UUID.randomUUID();
    Jwt jwt =
        new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(60),
            Map.of("alg", "none"),
            Map.of("sub", "user", "organization_ids", List.of(organization.toString())));
    var allowed = new UsernamePasswordAuthenticationToken(jwt, "token", List.of());
    assertDoesNotThrow(() -> new CatalogOwnership().requireOrganization(allowed, organization));
    assertThrows(
        AccessDeniedException.class,
        () -> new CatalogOwnership().requireOrganization(allowed, UUID.randomUUID()));
  }
}

package app.eventra.platform.catalog.service;

import java.util.Collection;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CatalogOwnership {
  public void requireOrganization(Authentication authentication, UUID organizationId) {
    if (authentication.getAuthorities().stream()
        .anyMatch(
            a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SERVICE")))
      return;
    Object principal = authentication.getPrincipal();
    if (principal instanceof Jwt jwt) {
      Object ids = jwt.getClaim("organization_ids");
      if (ids instanceof Collection<?> values
          && values.stream().map(String::valueOf).anyMatch(organizationId.toString()::equals))
        return;
      String id = jwt.getClaimAsString("organization_id");
      if (organizationId.toString().equals(id)) return;
    }
    throw new AccessDeniedException("organization is not owned by the authenticated subject");
  }

  public UUID user(Authentication authentication) {
    return UUID.nameUUIDFromBytes(
        authentication.getName().getBytes(java.nio.charset.StandardCharsets.UTF_8));
  }
}

package app.eventra.platform.identity.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.time.Instant;
import java.util.UUID;

public final class IdentityDtos {
  private IdentityDtos() {}

  public record ProfileResponse(UUID id, String subject, String email) {}

  public record UpdateProfileRequest(@NotBlank @Email String email) {}

  public record PreferencesResponse(
      String locale, String timezone, boolean marketingEmail, boolean marketingSms) {}

  public record UpdatePreferencesRequest(
      @NotBlank String locale,
      @NotBlank String timezone,
      boolean marketingEmail,
      boolean marketingSms) {}

  public record OrganizationRequest(@NotBlank @Size(max = 200) String name) {}

  public record OrganizationResponse(UUID id, String name, UUID createdBy) {}

  public record MembershipRequest(@NotNull UUID userId, @NotBlank String role) {}

  public record RoleRequest(@NotBlank String roleCode) {}

  public record VerificationRequest(@NotBlank String caseType) {}

  public record VerificationResponse(UUID id, String caseType, String status) {}

  public record AddressRequest(
      @NotBlank String addressType,
      @NotBlank String line1,
      String line2,
      @NotBlank String city,
      String region,
      @NotBlank String postalCode,
      @NotBlank @Size(min = 2, max = 2) String countryCode) {}

  public record AddressResponse(
      UUID id,
      String addressType,
      String line1,
      String line2,
      String city,
      String region,
      String postalCode,
      String countryCode) {}

  public record DocumentRequest(
      @NotBlank String objectStorageKey,
      @NotBlank String documentType,
      @NotBlank String contentType) {}

  public record StorageAuthorizationRequest(
      @NotBlank @jakarta.validation.constraints.Pattern(regexp = "application/pdf|image/(png|jpeg)")
          String contentType) {}

  public record SignedStorageResponse(
      String objectStorageKey, URI url, Instant expiresAt, String method) {}

  public record DocumentResponse(
      UUID id,
      UUID caseId,
      String documentType,
      String contentType,
      URI downloadUrl,
      Instant expiresAt) {}
}

package app.eventra.platform.catalog.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.time.Instant;
import java.util.UUID;

public final class CatalogDtos {
  private CatalogDtos() {}

  public record EventRequest(
      @NotBlank @Size(max = 240) String title,
      @Size(max = 10000) String description,
      @NotBlank String visibility,
      @NotNull Instant startsAt,
      @NotNull Instant endsAt,
      @NotBlank String timezone,
      @NotNull UUID organizerOrganizationId) {}

  public record EventUpdateRequest(
      @NotBlank @Size(max = 240) String title,
      @Size(max = 10000) String description,
      @NotNull Instant startsAt,
      @NotNull Instant endsAt,
      @NotBlank String timezone) {}

  public record EventResponse(
      UUID id,
      UUID organizerOrganizationId,
      String title,
      String description,
      String status,
      String visibility,
      Instant startsAt,
      Instant endsAt,
      String timezone,
      long version) {}

  public record VenueRequest(
      @NotNull UUID ownerOrganizationId,
      @NotBlank String name,
      @NotBlank String city,
      @NotBlank String address) {}

  public record VenueResponse(
      UUID id, UUID ownerOrganizationId, String name, String city, String address, String status) {}

  public record ProviderRequest(
      @NotNull UUID ownerOrganizationId, @NotBlank String displayName, String bio) {}

  public record ProviderResponse(
      UUID id,
      UUID ownerOrganizationId,
      String displayName,
      String verificationStatus,
      boolean active) {}

  public record ServiceRequest(
      @NotNull UUID providerId,
      @NotBlank String name,
      @NotBlank String pricingModel,
      @PositiveOrZero long baseAmountMinor,
      @Pattern(regexp = "[A-Z]{3}") String currency) {}

  public record ServiceResponse(
      UUID id,
      UUID providerId,
      String name,
      String pricingModel,
      long baseAmountMinor,
      String currency) {}

  public record ReviewRequest(
      UUID providerId,
      UUID venueId,
      @NotBlank String bookingReference,
      @NotNull UUID authorUserId,
      @Min(1) @Max(5) short rating,
      @NotBlank String body) {}

  public record ReviewResponse(
      UUID id, UUID providerId, UUID venueId, short rating, String body, String moderationState) {}

  public record PackageRequest(
      @NotNull UUID providerServiceId,
      @NotBlank String name,
      @PositiveOrZero long amountMinor,
      @Pattern(regexp = "[A-Z]{3}") String currency) {}

  public record PackageResponse(
      UUID id, UUID providerServiceId, String name, long amountMinor, String currency) {}

  public record ReplyRequest(@NotNull UUID authorOrganizationId, @NotBlank String body) {}

  public record MediaRequest(
      @NotBlank String objectStorageKey,
      @NotBlank String mediaType,
      @PositiveOrZero int sortOrder) {}

  public record PortfolioRequest(@NotBlank String objectStorageKey, String caption) {}

  public record ModerationRequest(@NotBlank String state) {}

  public record StorageAuthorizationRequest(
      @NotBlank @Pattern(regexp = "image/(png|jpeg|webp)|video/mp4") String contentType) {}

  public record SignedStorageResponse(
      String objectStorageKey, URI url, Instant expiresAt, String method) {}

  public record MediaResponse(
      UUID id, UUID eventId, String mediaType, int sortOrder, URI downloadUrl, Instant expiresAt) {}

  public record PortfolioResponse(
      UUID id, UUID providerId, String caption, URI downloadUrl, Instant expiresAt) {}

  public record ModerationHistoryResponse(
      UUID id, String fromState, String toState, Instant changedAt) {}
}

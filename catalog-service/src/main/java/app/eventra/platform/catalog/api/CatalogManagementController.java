package app.eventra.platform.catalog.api;

import app.eventra.platform.catalog.api.CatalogDtos.*;
import app.eventra.platform.catalog.api.CatalogDtos.MediaRequest;
import app.eventra.platform.catalog.api.CatalogDtos.MediaResponse;
import app.eventra.platform.catalog.api.CatalogDtos.ModerationHistoryResponse;
import app.eventra.platform.catalog.api.CatalogDtos.ModerationRequest;
import app.eventra.platform.catalog.api.CatalogDtos.PackageRequest;
import app.eventra.platform.catalog.api.CatalogDtos.PackageResponse;
import app.eventra.platform.catalog.api.CatalogDtos.PortfolioRequest;
import app.eventra.platform.catalog.api.CatalogDtos.PortfolioResponse;
import app.eventra.platform.catalog.api.CatalogDtos.ProviderRequest;
import app.eventra.platform.catalog.api.CatalogDtos.ProviderResponse;
import app.eventra.platform.catalog.api.CatalogDtos.ReplyRequest;
import app.eventra.platform.catalog.api.CatalogDtos.ReviewRequest;
import app.eventra.platform.catalog.api.CatalogDtos.ReviewResponse;
import app.eventra.platform.catalog.api.CatalogDtos.ServiceRequest;
import app.eventra.platform.catalog.api.CatalogDtos.ServiceResponse;
import app.eventra.platform.catalog.api.CatalogDtos.VenueRequest;
import app.eventra.platform.catalog.api.CatalogDtos.VenueResponse;
import app.eventra.platform.catalog.domain.CatalogOutboxEvent;
import app.eventra.platform.catalog.domain.EventMedia;
import app.eventra.platform.catalog.domain.PortfolioItem;
import app.eventra.platform.catalog.domain.ProviderProfile;
import app.eventra.platform.catalog.domain.ProviderService;
import app.eventra.platform.catalog.domain.Review;
import app.eventra.platform.catalog.domain.ReviewModerationHistory;
import app.eventra.platform.catalog.domain.ReviewReply;
import app.eventra.platform.catalog.domain.ServicePackage;
import app.eventra.platform.catalog.domain.Venue;
import app.eventra.platform.catalog.repository.CatalogOutboxEventRepository;
import app.eventra.platform.catalog.repository.EventMediaRepository;
import app.eventra.platform.catalog.repository.EventRepository;
import app.eventra.platform.catalog.repository.PortfolioItemRepository;
import app.eventra.platform.catalog.repository.ProviderProfileRepository;
import app.eventra.platform.catalog.repository.ProviderServiceRepository;
import app.eventra.platform.catalog.repository.ReviewModerationHistoryRepository;
import app.eventra.platform.catalog.repository.ReviewReplyRepository;
import app.eventra.platform.catalog.repository.ReviewRepository;
import app.eventra.platform.catalog.repository.ServicePackageRepository;
import app.eventra.platform.catalog.repository.VenueRepository;
import app.eventra.platform.catalog.service.CatalogOwnership;
import app.eventra.platform.common.storage.ObjectStorageUrlSigner;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@Transactional
public class CatalogManagementController {
  private final VenueRepository venues;
  private final ProviderProfileRepository providers;
  private final ProviderServiceRepository services;
  private final ReviewRepository reviews;
  private final ServicePackageRepository packages;
  private final ReviewReplyRepository replies;
  private final EventMediaRepository media;
  private final PortfolioItemRepository portfolio;
  private final EventRepository events;
  private final ReviewModerationHistoryRepository histories;
  private final CatalogOutboxEventRepository outbox;
  private final ObjectStorageUrlSigner storage;
  private final CatalogOwnership ownership;

  public CatalogManagementController(
      VenueRepository venues,
      ProviderProfileRepository providers,
      ProviderServiceRepository services,
      ReviewRepository reviews,
      ServicePackageRepository packages,
      ReviewReplyRepository replies,
      EventMediaRepository media,
      PortfolioItemRepository portfolio,
      EventRepository events,
      ReviewModerationHistoryRepository histories,
      CatalogOutboxEventRepository outbox,
      ObjectStorageUrlSigner storage,
      CatalogOwnership ownership) {
    this.venues = venues;
    this.providers = providers;
    this.services = services;
    this.packages = packages;
    this.reviews = reviews;
    this.replies = replies;
    this.media = media;
    this.portfolio = portfolio;
    this.events = events;
    this.histories = histories;
    this.outbox = outbox;
    this.storage = storage;
    this.ownership = ownership;
  }

  @PostMapping("/venues")
  public ResponseEntity<VenueResponse> createVenue(
      @Valid @RequestBody VenueRequest r, Authentication auth) {
    ownership.requireOrganization(auth, r.ownerOrganizationId());
    Venue v =
        venues.save(
            new Venue(UUID.randomUUID(), r.ownerOrganizationId(), r.name(), r.city(), r.address()));
    return ResponseEntity.created(URI.create("/v1/venues/" + v.getId())).body(venue(v));
  }

  @GetMapping("/venues")
  public List<VenueResponse> venues(@RequestParam String city) {
    return venues.findByCityIgnoreCaseAndStatus(city, "ACTIVE").stream().map(this::venue).toList();
  }

  @PostMapping("/providers")
  public ResponseEntity<ProviderResponse> createProvider(
      @Valid @RequestBody ProviderRequest r, Authentication auth) {
    ownership.requireOrganization(auth, r.ownerOrganizationId());
    ProviderProfile p =
        providers.save(
            new ProviderProfile(
                UUID.randomUUID(), r.ownerOrganizationId(), r.displayName(), r.bio()));
    return ResponseEntity.created(URI.create("/v1/providers/" + p.getId())).body(provider(p));
  }

  @GetMapping("/providers")
  public List<ProviderResponse> providers() {
    return providers.findByActiveTrueAndVerificationStatus("APPROVED").stream()
        .map(this::provider)
        .toList();
  }

  @PostMapping("/services")
  public ResponseEntity<ServiceResponse> createService(@Valid @RequestBody ServiceRequest r) {
    ProviderService s =
        services.save(
            new ProviderService(
                UUID.randomUUID(),
                r.providerId(),
                r.name(),
                r.pricingModel(),
                r.baseAmountMinor(),
                r.currency()));
    return ResponseEntity.created(URI.create("/v1/services/" + s.getId())).body(service(s));
  }

  @GetMapping("/providers/{providerId}/services")
  public List<ServiceResponse> services(@PathVariable UUID providerId) {
    return services.findByProviderIdAndActiveTrue(providerId).stream().map(this::service).toList();
  }

  @PostMapping("/packages")
  public ResponseEntity<PackageResponse> createPackage(@Valid @RequestBody PackageRequest r) {
    ServicePackage p =
        packages.save(
            new ServicePackage(
                UUID.randomUUID(), r.providerServiceId(), r.name(), r.amountMinor(), r.currency()));
    return ResponseEntity.created(URI.create("/v1/packages/" + p.getId())).body(pack(p));
  }

  @GetMapping("/services/{serviceId}/packages")
  public List<PackageResponse> packages(@PathVariable UUID serviceId) {
    return packages.findByProviderServiceIdAndActiveTrue(serviceId).stream()
        .map(this::pack)
        .toList();
  }

  @PostMapping("/reviews")
  public ResponseEntity<ReviewResponse> review(
      @Valid @RequestBody ReviewRequest r, Authentication auth) {
    Review review =
        reviews.save(
            new Review(
                UUID.randomUUID(),
                r.providerId(),
                r.venueId(),
                r.bookingReference(),
                ownership.user(auth),
                r.rating(),
                r.body()));
    return ResponseEntity.created(URI.create("/v1/reviews/" + review.getId())).body(review(review));
  }

  @PostMapping("/reviews/{reviewId}/replies")
  public ResponseEntity<Void> reply(
      @PathVariable UUID reviewId, @Valid @RequestBody ReplyRequest r) {
    if (!reviews.existsById(reviewId)) return ResponseEntity.notFound().build();
    replies.save(new ReviewReply(UUID.randomUUID(), reviewId, r.authorOrganizationId(), r.body()));
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/events/{eventId}/media-upload-authorizations")
  public SignedStorageResponse authorizeMedia(
      @PathVariable UUID eventId,
      @Valid @RequestBody StorageAuthorizationRequest r,
      Authentication auth) {
    var event = events.findById(eventId).orElseThrow();
    ownership.requireOrganization(auth, event.getOrganizerOrganizationId());
    String key = "events/" + eventId + "/" + UUID.randomUUID();
    var signed = storage.signPut(key, r.contentType());
    return new SignedStorageResponse(key, signed.url(), signed.expiresAt(), signed.method());
  }

  @PostMapping("/events/{eventId}/media")
  public ResponseEntity<Void> addMedia(
      @PathVariable UUID eventId, @Valid @RequestBody MediaRequest r, Authentication auth) {
    var event = events.findById(eventId).orElseThrow();
    ownership.requireOrganization(auth, event.getOrganizerOrganizationId());
    if (!r.objectStorageKey().startsWith("events/" + eventId + "/"))
      return ResponseEntity.unprocessableEntity().build();
    media.save(
        new EventMedia(
            UUID.randomUUID(), eventId, r.objectStorageKey(), r.mediaType(), r.sortOrder()));
    emit("EventMedia", eventId.toString());
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/events/{eventId}/media")
  public List<MediaResponse> listMedia(@PathVariable UUID eventId) {
    return media.findByEventIdOrderBySortOrder(eventId).stream()
        .map(
            m -> {
              var signed = storage.signGet(m.getObjectStorageKey());
              return new MediaResponse(
                  m.getId(),
                  m.getEventId(),
                  m.getMediaType(),
                  m.getSortOrder(),
                  signed.url(),
                  signed.expiresAt());
            })
        .toList();
  }

  @PostMapping("/providers/{providerId}/portfolio-upload-authorizations")
  public SignedStorageResponse authorizePortfolio(
      @PathVariable UUID providerId,
      @Valid @RequestBody StorageAuthorizationRequest r,
      Authentication auth) {
    var provider = providers.findById(providerId).orElseThrow();
    ownership.requireOrganization(auth, provider.getOwnerOrganizationId());
    String key = "providers/" + providerId + "/" + UUID.randomUUID();
    var signed = storage.signPut(key, r.contentType());
    return new SignedStorageResponse(key, signed.url(), signed.expiresAt(), signed.method());
  }

  @PostMapping("/providers/{providerId}/portfolio")
  public ResponseEntity<Void> addPortfolio(
      @PathVariable UUID providerId, @Valid @RequestBody PortfolioRequest r, Authentication auth) {
    var provider = providers.findById(providerId).orElseThrow();
    ownership.requireOrganization(auth, provider.getOwnerOrganizationId());
    if (!r.objectStorageKey().startsWith("providers/" + providerId + "/"))
      return ResponseEntity.unprocessableEntity().build();
    portfolio.save(
        new PortfolioItem(UUID.randomUUID(), providerId, r.objectStorageKey(), r.caption()));
    emit("PortfolioItem", providerId.toString());
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/providers/{providerId}/portfolio")
  public List<PortfolioResponse> listPortfolio(@PathVariable UUID providerId) {
    return portfolio.findByProviderId(providerId).stream()
        .map(
            p -> {
              var signed = storage.signGet(p.getObjectStorageKey());
              return new PortfolioResponse(
                  p.getId(), p.getProviderId(), p.getCaption(), signed.url(), signed.expiresAt());
            })
        .toList();
  }

  @PostMapping("/reviews/{reviewId}/moderation")
  public ResponseEntity<Void> moderate(
      @PathVariable UUID reviewId, @Valid @RequestBody ModerationRequest r) {
    if (!Set.of("PENDING", "APPROVED", "REJECTED").contains(r.state()))
      return ResponseEntity.unprocessableEntity().build();
    return reviews
        .findById(reviewId)
        .map(
            review -> {
              String from = review.getModerationState();
              review.moderate(r.state());
              reviews.save(review);
              histories.save(
                  new ReviewModerationHistory(
                      UUID.randomUUID(), reviewId, from, r.state(), "api-user"));
              emit("Review", reviewId.toString());
              return ResponseEntity.noContent().<Void>build();
            })
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/reviews/{reviewId}/moderation-history")
  public List<ModerationHistoryResponse> history(@PathVariable UUID reviewId) {
    return histories.findByReviewIdOrderByChangedAtDesc(reviewId).stream()
        .map(
            h ->
                new ModerationHistoryResponse(
                    h.getId(), h.getFromState(), h.getToState(), h.getChangedAt()))
        .toList();
  }

  private void emit(String aggregate, String id) {
    outbox.save(
        new CatalogOutboxEvent(
            UUID.randomUUID(), aggregate, id, aggregate + "ChangedV1", "{\"id\":\"" + id + "\"}"));
  }

  private VenueResponse venue(Venue v) {
    return new VenueResponse(
        v.getId(),
        v.getOwnerOrganizationId(),
        v.getName(),
        v.getCity(),
        v.getAddress(),
        v.getStatus());
  }

  private ProviderResponse provider(ProviderProfile p) {
    return new ProviderResponse(
        p.getId(),
        p.getOwnerOrganizationId(),
        p.getDisplayName(),
        p.getVerificationStatus(),
        p.isActive());
  }

  private ServiceResponse service(ProviderService s) {
    return new ServiceResponse(
        s.getId(),
        s.getProviderId(),
        s.getName(),
        s.getPricingModel(),
        s.getBaseAmountMinor(),
        s.getCurrency());
  }

  private PackageResponse pack(ServicePackage p) {
    return new PackageResponse(
        p.getId(), p.getProviderServiceId(), p.getName(), p.getAmountMinor(), p.getCurrency());
  }

  private ReviewResponse review(Review r) {
    return new ReviewResponse(
        r.getId(),
        r.getProviderId(),
        r.getVenueId(),
        r.getRating(),
        r.getBody(),
        r.getModerationState());
  }
}

package app.eventra.platform.identity.api;

import app.eventra.platform.common.storage.ObjectStorageUrlSigner;
import app.eventra.platform.identity.api.IdentityDtos.AddressRequest;
import app.eventra.platform.identity.api.IdentityDtos.AddressResponse;
import app.eventra.platform.identity.api.IdentityDtos.DocumentRequest;
import app.eventra.platform.identity.api.IdentityDtos.MembershipRequest;
import app.eventra.platform.identity.api.IdentityDtos.OrganizationRequest;
import app.eventra.platform.identity.api.IdentityDtos.OrganizationResponse;
import app.eventra.platform.identity.api.IdentityDtos.PreferencesResponse;
import app.eventra.platform.identity.api.IdentityDtos.ProfileResponse;
import app.eventra.platform.identity.api.IdentityDtos.RoleRequest;
import app.eventra.platform.identity.api.IdentityDtos.UpdatePreferencesRequest;
import app.eventra.platform.identity.api.IdentityDtos.UpdateProfileRequest;
import app.eventra.platform.identity.api.IdentityDtos.VerificationRequest;
import app.eventra.platform.identity.api.IdentityDtos.VerificationResponse;
import app.eventra.platform.identity.domain.Address;
import app.eventra.platform.identity.domain.Organization;
import app.eventra.platform.identity.domain.OrganizationMember;
import app.eventra.platform.identity.domain.Role;
import app.eventra.platform.identity.domain.UserAccount;
import app.eventra.platform.identity.domain.UserPreference;
import app.eventra.platform.identity.domain.UserRole;
import app.eventra.platform.identity.domain.VerificationCase;
import app.eventra.platform.identity.domain.VerificationDocumentReference;
import app.eventra.platform.identity.repository.AddressRepository;
import app.eventra.platform.identity.repository.OrganizationMemberRepository;
import app.eventra.platform.identity.repository.OrganizationRepository;
import app.eventra.platform.identity.repository.RoleRepository;
import app.eventra.platform.identity.repository.UserAccountRepository;
import app.eventra.platform.identity.repository.UserPreferenceRepository;
import app.eventra.platform.identity.repository.UserRoleRepository;
import app.eventra.platform.identity.repository.VerificationCaseRepository;
import app.eventra.platform.identity.repository.VerificationDocumentReferenceRepository;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class IdentityController {
  private final UserAccountRepository users;
  private final UserPreferenceRepository preferences;
  private final OrganizationRepository organizations;
  private final RoleRepository roles;
  private final UserRoleRepository userRoles;
  private final OrganizationMemberRepository members;
  private final VerificationCaseRepository verificationCases;
  private final AddressRepository addresses;
  private final VerificationDocumentReferenceRepository documents;
  private final ObjectStorageUrlSigner storage;

  public IdentityController(
      UserAccountRepository users,
      UserPreferenceRepository preferences,
      OrganizationRepository organizations,
      RoleRepository roles,
      UserRoleRepository userRoles,
      OrganizationMemberRepository members,
      VerificationCaseRepository verificationCases,
      AddressRepository addresses,
      VerificationDocumentReferenceRepository documents,
      ObjectStorageUrlSigner storage) {
    this.users = users;
    this.preferences = preferences;
    this.organizations = organizations;
    this.roles = roles;
    this.userRoles = userRoles;
    this.members = members;
    this.verificationCases = verificationCases;
    this.addresses = addresses;
    this.documents = documents;
    this.storage = storage;
  }

  @GetMapping("/users/me")
  public ProfileResponse profile(Authentication auth) {
    UserAccount user = user(auth);
    return new ProfileResponse(user.getId(), user.getSubject(), user.getEmailDisplay());
  }

  @PutMapping("/users/me")
  public ProfileResponse update(
      @Valid @RequestBody UpdateProfileRequest request, Authentication auth) {
    UserAccount user = user(auth);
    user.updateEmail(request.email());
    users.save(user);
    return new ProfileResponse(user.getId(), user.getSubject(), user.getEmailDisplay());
  }

  @GetMapping("/users/me/preferences")
  public PreferencesResponse preferences(Authentication auth) {
    UserPreference p =
        preferences
            .findById(user(auth).getId())
            .orElseGet(() -> preferences.save(new UserPreference(user(auth).getId())));
    return new PreferencesResponse(
        p.getLocale(), p.getTimezone(), p.isMarketingEmail(), p.isMarketingSms());
  }

  @PutMapping("/users/me/preferences")
  public PreferencesResponse updatePreferences(
      @Valid @RequestBody UpdatePreferencesRequest request, Authentication auth) {
    UUID id = user(auth).getId();
    UserPreference p = preferences.findById(id).orElseGet(() -> new UserPreference(id));
    p.update(
        request.locale(), request.timezone(), request.marketingEmail(), request.marketingSms());
    preferences.save(p);
    return new PreferencesResponse(
        p.getLocale(), p.getTimezone(), p.isMarketingEmail(), p.isMarketingSms());
  }

  @PostMapping("/organizations")
  public ResponseEntity<OrganizationResponse> createOrganization(
      @Valid @RequestBody OrganizationRequest request, Authentication auth) {
    UserAccount user = user(auth);
    Organization organization =
        organizations.save(new Organization(UUID.randomUUID(), request.name(), user.getId()));
    OrganizationResponse body =
        new OrganizationResponse(
            organization.getId(), organization.getName(), organization.getCreatedBy());
    return ResponseEntity.created(URI.create("/v1/organizations/" + organization.getId()))
        .body(body);
  }

  @PostMapping("/organizations/{organizationId}/members")
  public ResponseEntity<Void> addMember(
      @PathVariable UUID organizationId,
      @Valid @RequestBody MembershipRequest request,
      Authentication auth) {
    UserAccount actor = user(auth);
    if (!members.existsByOrganizationIdAndUserId(organizationId, actor.getId()))
      return ResponseEntity.status(403).build();
    members.save(new OrganizationMember(organizationId, request.userId(), request.role()));
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/users/{userId}/roles")
  public ResponseEntity<Void> assignRole(
      @PathVariable UUID userId, @Valid @RequestBody RoleRequest request, Authentication auth) {
    UserAccount actor = user(auth);
    UUID adminRoleId = UUID.fromString("00000000-0000-0000-0000-000000000004");
    if (!userRoles.existsByUserIdAndRoleId(actor.getId(), adminRoleId))
      return ResponseEntity.status(403).build();
    Role role = roles.findByCode(request.roleCode()).orElseThrow();
    userRoles.save(new UserRole(userId, role.getId(), actor.getSubject()));
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/users/me/verification-cases")
  public ResponseEntity<VerificationResponse> submitVerification(
      @Valid @RequestBody VerificationRequest request, Authentication auth) {
    VerificationCase c =
        verificationCases.save(
            new VerificationCase(UUID.randomUUID(), user(auth).getId(), request.caseType()));
    return ResponseEntity.created(URI.create("/v1/verification-cases/" + c.getId()))
        .body(new VerificationResponse(c.getId(), c.getCaseType(), c.getStatus()));
  }

  @GetMapping("/users/me/verification-cases")
  public List<VerificationResponse> verifications(Authentication auth) {
    return verificationCases.findByUserIdOrderByCreatedAtDesc(user(auth).getId()).stream()
        .map(c -> new VerificationResponse(c.getId(), c.getCaseType(), c.getStatus()))
        .toList();
  }

  @PostMapping("/users/me/addresses")
  public ResponseEntity<AddressResponse> addAddress(
      @Valid @RequestBody AddressRequest r, Authentication auth) {
    Address a =
        addresses.save(
            new Address(
                UUID.randomUUID(),
                user(auth).getId(),
                r.addressType(),
                r.line1(),
                r.line2(),
                r.city(),
                r.region(),
                r.postalCode(),
                r.countryCode()));
    return ResponseEntity.created(URI.create("/v1/addresses/" + a.getId())).body(address(a));
  }

  @GetMapping("/users/me/addresses")
  public List<AddressResponse> addressList(Authentication auth) {
    return addresses.findByUserId(user(auth).getId()).stream().map(this::address).toList();
  }

  @PostMapping("/verification-cases/{caseId}/document-upload-authorizations")
  public IdentityDtos.SignedStorageResponse authorizeDocumentUpload(
      @PathVariable UUID caseId,
      @Valid @RequestBody IdentityDtos.StorageAuthorizationRequest r,
      Authentication auth) {
    requireCaseOwner(caseId, auth);
    String key = "verification/" + caseId + "/" + UUID.randomUUID();
    var signed = storage.signPut(key, r.contentType());
    return new IdentityDtos.SignedStorageResponse(
        key, signed.url(), signed.expiresAt(), signed.method());
  }

  @PostMapping("/verification-cases/{caseId}/documents")
  public ResponseEntity<Void> addDocument(
      @PathVariable UUID caseId, @Valid @RequestBody DocumentRequest r, Authentication auth) {
    requireCaseOwner(caseId, auth);
    if (!r.objectStorageKey().startsWith("verification/" + caseId + "/"))
      return ResponseEntity.unprocessableEntity().build();
    documents.save(
        new VerificationDocumentReference(
            UUID.randomUUID(), caseId, r.objectStorageKey(), r.documentType(), r.contentType()));
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/verification-cases/{caseId}/documents")
  public List<IdentityDtos.DocumentResponse> documents(
      @PathVariable UUID caseId, Authentication auth) {
    requireCaseOwner(caseId, auth);
    return documents.findByCaseId(caseId).stream()
        .map(
            d -> {
              var signed = storage.signGet(d.getObjectStorageKey());
              return new IdentityDtos.DocumentResponse(
                  d.getId(),
                  d.getCaseId(),
                  d.getDocumentType(),
                  d.getContentType(),
                  signed.url(),
                  signed.expiresAt());
            })
        .toList();
  }

  private AddressResponse address(Address a) {
    return new AddressResponse(
        a.getId(),
        a.getAddressType(),
        a.getLine1(),
        a.getLine2(),
        a.getCity(),
        a.getRegion(),
        a.getPostalCode(),
        a.getCountryCode());
  }

  private void requireCaseOwner(UUID caseId, Authentication auth) {
    VerificationCase c = verificationCases.findById(caseId).orElseThrow();
    if (!c.getUserId().equals(user(auth).getId()))
      throw new org.springframework.security.access.AccessDeniedException(
          "verification case is owned by another user");
  }

  private UserAccount user(Authentication auth) {
    return users
        .findBySubject(auth.getName())
        .orElseGet(
            () ->
                users.save(
                    new UserAccount(
                        UUID.randomUUID(), auth.getName(), auth.getName() + "@unverified.local")));
  }
}

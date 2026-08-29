package app.eventra.platform.identity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "address", schema = "eventra_identity")
public class Address {
  @Id private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "address_type", nullable = false)
  private String addressType;

  @Column(nullable = false)
  private String line1;

  private String line2;

  @Column(nullable = false)
  private String city;

  private String region;

  @Column(name = "postal_code", nullable = false)
  private String postalCode;

  @Column(name = "country_code", nullable = false)
  private String countryCode;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected Address() {}

  public Address(
      UUID id,
      UUID user,
      String type,
      String line1,
      String line2,
      String city,
      String region,
      String postal,
      String country) {
    this.id = id;
    userId = user;
    addressType = type;
    this.line1 = line1;
    this.line2 = line2;
    this.city = city;
    this.region = region;
    postalCode = postal;
    countryCode = country;
    createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public String getAddressType() {
    return addressType;
  }

  public String getLine1() {
    return line1;
  }

  public String getLine2() {
    return line2;
  }

  public String getCity() {
    return city;
  }

  public String getRegion() {
    return region;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public String getCountryCode() {
    return countryCode;
  }
}

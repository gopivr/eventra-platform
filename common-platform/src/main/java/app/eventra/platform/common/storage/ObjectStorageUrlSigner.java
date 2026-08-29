package app.eventra.platform.common.storage;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ObjectStorageUrlSigner {
  private final String baseUrl;
  private final byte[] secret;
  private final Duration lifetime;

  public ObjectStorageUrlSigner(
      @Value("${object-storage.base-url:http://localhost:9000/eventra}") String baseUrl,
      @Value(
              "${object-storage.signing-secret:${OBJECT_STORAGE_SIGNING_SECRET:local_storage_signing_secret}}")
          String secret,
      @Value("${object-storage.url-lifetime:PT10M}") Duration lifetime) {
    this.baseUrl = baseUrl.replaceAll("/$", "");
    if (secret.length() < 16)
      throw new IllegalStateException(
          "object storage signing secret must contain at least 16 characters");
    this.secret = secret.getBytes(StandardCharsets.UTF_8);
    this.lifetime = lifetime;
  }

  public SignedUrl signGet(String key) {
    return sign("GET", key, null);
  }

  public SignedUrl signPut(String key, String contentType) {
    return sign("PUT", key, contentType);
  }

  private SignedUrl sign(String method, String key, String contentType) {
    validate(key);
    Instant expires = Instant.now().plus(lifetime);
    String canonical =
        method
            + "\n"
            + key
            + "\n"
            + expires.getEpochSecond()
            + "\n"
            + (contentType == null ? "" : contentType);
    String signature = hmac(canonical);
    String encoded =
        java.util.Arrays.stream(key.split("/", -1))
            .map(part -> URLEncoder.encode(part, StandardCharsets.UTF_8).replace("+", "%20"))
            .collect(java.util.stream.Collectors.joining("/"));
    String query =
        "expires="
            + expires.getEpochSecond()
            + "&signature="
            + signature
            + (contentType == null
                ? ""
                : "&contentType=" + URLEncoder.encode(contentType, StandardCharsets.UTF_8));
    return new SignedUrl(URI.create(baseUrl + "/" + encoded + "?" + query), expires, method, key);
  }

  private void validate(String key) {
    if (key == null
        || key.isBlank()
        || key.startsWith("/")
        || key.contains("..")
        || key.contains("\\")) throw new IllegalArgumentException("invalid object storage key");
  }

  private String hmac(String value) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret, "HmacSHA256"));
      return HexFormat.of().formatHex(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
    } catch (java.security.GeneralSecurityException impossible) {
      throw new IllegalStateException(impossible);
    }
  }

  public record SignedUrl(URI url, Instant expiresAt, String method, String objectStorageKey) {}
}

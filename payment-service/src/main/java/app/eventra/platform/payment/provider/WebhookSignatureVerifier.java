package app.eventra.platform.payment.provider;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class WebhookSignatureVerifier {
  private final Environment environment;

  public WebhookSignatureVerifier(Environment environment) {
    this.environment = environment;
  }

  public void verify(String provider, String payload, String supplied, long timestamp) {
    if (Math.abs(System.currentTimeMillis() / 1000 - timestamp) > 300)
      throw new IllegalArgumentException("webhook timestamp expired");
    String secret =
        environment.getProperty(
            "payment.providers." + provider + ".webhook-secret",
            environment.getProperty("payment.webhook-secret", ""));
    if (secret.isBlank() || supplied == null || supplied.isBlank())
      throw new IllegalArgumentException("webhook signature is not configured");
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      byte[] expected = mac.doFinal((timestamp + "." + payload).getBytes(StandardCharsets.UTF_8));
      byte[] actual;
      try {
        actual = HexFormat.of().parseHex(supplied.trim());
      } catch (IllegalArgumentException malformed) {
        throw new IllegalArgumentException("invalid webhook signature");
      }
      if (!MessageDigest.isEqual(expected, actual))
        throw new IllegalArgumentException("invalid webhook signature");
    } catch (GeneralSecurityException impossible) {
      throw new IllegalStateException("webhook verification unavailable", impossible);
    }
  }
}

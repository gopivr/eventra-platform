package app.eventra.platform.payment.provider;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

class WebhookSignatureVerifierTest {
  @Test
  void verifiesProviderSpecificHmacAndRejectsTampering() throws Exception {
    String provider = "acme", secret = "provider-secret", payload = "{\"id\":1}";
    long timestamp = System.currentTimeMillis() / 1000;
    MockEnvironment env =
        new MockEnvironment().withProperty("payment.providers.acme.webhook-secret", secret);
    WebhookSignatureVerifier verifier = new WebhookSignatureVerifier(env);
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
    String signature =
        HexFormat.of()
            .formatHex(mac.doFinal((timestamp + "." + payload).getBytes(StandardCharsets.UTF_8)));
    assertDoesNotThrow(() -> verifier.verify(provider, payload, signature, timestamp));
    assertThrows(
        IllegalArgumentException.class,
        () -> verifier.verify(provider, payload + "x", signature, timestamp));
    assertThrows(
        IllegalArgumentException.class,
        () -> verifier.verify(provider, payload, "not-hex", timestamp));
  }
}

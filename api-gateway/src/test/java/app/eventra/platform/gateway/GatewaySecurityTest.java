package app.eventra.platform.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(properties = "management.health.redis.enabled=false")
@AutoConfigureWebTestClient
class GatewaySecurityTest {
  @MockBean ReactiveJwtDecoder decoder;
  @Autowired WebTestClient client;

  @Test
  void protectedRouteRejectsMissingBearerToken() {
    client.get().uri("/v1/users/me").exchange().expectStatus().isUnauthorized();
  }

  @Test
  void healthIsPublic() {
    client.get().uri("/actuator/health").exchange().expectStatus().isOk();
  }
}

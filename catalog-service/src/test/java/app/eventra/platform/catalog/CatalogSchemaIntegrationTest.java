package app.eventra.platform.catalog;

import static org.assertj.core.api.Assertions.assertThat;

import app.eventra.platform.catalog.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class CatalogSchemaIntegrationTest {
  @Container
  static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
  }

  @MockBean JwtDecoder jwtDecoder;

  @Autowired EventRepository events;

  @Test
  void liquibaseSchemaMatchesJpaMappings() {}

  @Test
  void publicEventDiscoveryAcceptsAnOmittedCity() {
    assertThat(events.discover()).isEmpty();
    assertThat(events.discoverByCity("New York")).isEmpty();
  }
}

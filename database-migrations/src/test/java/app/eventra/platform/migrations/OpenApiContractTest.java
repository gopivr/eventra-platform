package app.eventra.platform.migrations;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.nio.file.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

class OpenApiContractTest {
  @Test
  void everyServiceContractIsOpenApi31WithUniqueOperationsAndBearerSecurity() throws Exception {
    Path root = Path.of("..").toAbsolutePath().normalize();
    Set<String> operationIds = new HashSet<>();
    for (String service :
        List.of(
            "identity-service",
            "catalog-service",
            "booking-service",
            "payment-service",
            "engagement-service")) {
      Path contract = root.resolve(service + "/src/main/resources/openapi.yaml");
      assertTrue(Files.isRegularFile(contract), service + " contract missing");
      Map<String, Object> document;
      try (InputStream input = Files.newInputStream(contract)) {
        document = new Yaml().load(input);
      }
      assertTrue(
          String.valueOf(document.get("openapi")).startsWith("3.1"),
          service + " must use OpenAPI 3.1");
      Map<?, ?> paths = (Map<?, ?>) document.get("paths");
      assertNotNull(paths);
      assertFalse(paths.isEmpty());
      for (Object pathValue : paths.values()) {
        Map<?, ?> operations = (Map<?, ?>) pathValue;
        for (Object operationValue : operations.values()) {
          if (!(operationValue instanceof Map<?, ?> operation)
              || !operation.containsKey("operationId")) continue;
          String id = String.valueOf(operation.get("operationId"));
          assertTrue(operationIds.add(id), "duplicate operationId: " + id);
          assertTrue(operation.containsKey("responses"), id + " has no responses");
        }
      }
      Map<?, ?> components = (Map<?, ?>) document.get("components");
      assertNotNull(components, service + " components missing");
      assertTrue(
          ((Map<?, ?>) components.get("securitySchemes")).containsKey("bearerAuth"),
          service + " bearerAuth missing");
    }
  }
}

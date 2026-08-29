package app.eventra.platform.migrations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
class LiquibaseMigrationTest {
  private static final Pattern TABLE_PATTERN =
      Pattern.compile(
          "CREATE\\s+TABLE\\s+(?:\\$\\{schemaName}|eventra_[a-z]+)\\.([a-zA-Z0-9_]+)",
          Pattern.CASE_INSENSITIVE);
  private static final Pattern INDEX_PATTERN =
      Pattern.compile(
          "CREATE\\s+(?:UNIQUE\\s+)?INDEX\\s+([a-zA-Z0-9_]+)", Pattern.CASE_INSENSITIVE);
  private static final Pattern CONSTRAINT_PATTERN =
      Pattern.compile("CONSTRAINT\\s+([a-zA-Z0-9_]+)", Pattern.CASE_INSENSITIVE);
  private static final Map<String, String> SCHEMAS =
      Map.of(
          "identity",
          "eventra_identity",
          "catalog",
          "eventra_catalog",
          "booking",
          "eventra_booking",
          "payment",
          "eventra_payment",
          "engagement",
          "eventra_engagement");

  @Test
  void rootChangelogAppliesTwiceAndCreatesEveryDeclaredSchemaObject() throws Exception {
    try (PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")) {
      postgres.start();
      try (Connection connection = postgres.createConnection("");
          Statement statement = connection.createStatement()) {
        Database database =
            DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));
        Liquibase liquibase =
            new Liquibase(
                "db/db.changelog-master.yaml", new ClassLoaderResourceAccessor(), database);
        liquibase.update("");
        liquibase.update("");
        assertServiceSchemasExist(statement);
        assertEveryDeclaredSchemaObjectExists(statement);
      }
    }
  }

  private void assertServiceSchemasExist(Statement statement) throws Exception {
    try (ResultSet schemas =
        statement.executeQuery(
            "select schema_name from information_schema.schemata where schema_name like 'eventra_%'")) {
      int count = 0;
      while (schemas.next()) {
        count++;
      }
      assertTrue(count >= SCHEMAS.size());
    }
  }

  private void assertEveryDeclaredSchemaObjectExists(Statement statement) throws Exception {
    URL resource = LiquibaseMigrationTest.class.getClassLoader().getResource("db/services");
    assertNotNull(resource, "Packaged service migrations are missing");
    Path services = Path.of(resource.toURI());
    for (Map.Entry<String, String> service : SCHEMAS.entrySet()) {
      Set<String> tables = new HashSet<>();
      Set<String> indexes = new HashSet<>();
      Set<String> constraints = new HashSet<>();
      try (var files = Files.walk(services.resolve(service.getKey()))) {
        for (Path file :
            files
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".yaml"))
                .toList()) {
          String migration = Files.readString(file);
          TABLE_PATTERN.matcher(migration).results().forEach(match -> tables.add(match.group(1)));
          INDEX_PATTERN.matcher(migration).results().forEach(match -> indexes.add(match.group(1)));
          CONSTRAINT_PATTERN
              .matcher(migration)
              .results()
              .forEach(match -> constraints.add(match.group(1)));
        }
      }
      for (String table : tables) {
        assertMetadataCount(
            statement,
            "select count(*) from information_schema.tables where table_schema='"
                + service.getValue()
                + "' and table_name='"
                + table
                + "'",
            service.getValue() + "." + table);
      }
      for (String index : indexes) {
        assertMetadataCount(
            statement,
            "select count(*) from pg_indexes where schemaname='"
                + service.getValue()
                + "' and indexname='"
                + index
                + "'",
            service.getValue() + "." + index);
      }
      for (String constraint : constraints) {
        assertMetadataCount(
            statement,
            "select count(*) from information_schema.table_constraints where constraint_schema='"
                + service.getValue()
                + "' and constraint_name='"
                + constraint
                + "'",
            service.getValue() + "." + constraint);
      }
    }
  }

  private void assertMetadataCount(Statement statement, String query, String object)
      throws Exception {
    try (ResultSet result = statement.executeQuery(query)) {
      result.next();
      assertEquals(1, result.getInt(1), object + " missing after migration");
    }
  }
}

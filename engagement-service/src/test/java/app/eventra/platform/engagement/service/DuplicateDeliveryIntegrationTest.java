package app.eventra.platform.engagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(properties = "spring.kafka.listener.auto-startup=false")
@Testcontainers(disabledWithoutDocker = true)
class DuplicateDeliveryIntegrationTest {
  @Container
  static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry r) {
    r.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    r.add("spring.datasource.username", POSTGRES::getUsername);
    r.add("spring.datasource.password", POSTGRES::getPassword);
  }

  @MockBean JwtDecoder jwtDecoder;
  @Autowired NotificationService notifications;
  @Autowired JdbcTemplate jdbc;

  @Test
  void repeatedTicketEventCreatesOneInboxRecordAndNotification() {
    UUID user = UUID.randomUUID(), ticket = UUID.randomUUID();
    String message = UUID.randomUUID().toString();
    notifications.ticketIssued(message, user, UUID.randomUUID().toString(), ticket);
    notifications.ticketIssued(message, user, UUID.randomUUID().toString(), ticket);
    assertEquals(
        1,
        jdbc.queryForObject(
            "select count(*) from eventra_engagement.inbox_message where message_id=?",
            Integer.class,
            message));
    assertEquals(
        1,
        jdbc.queryForObject(
            "select count(*) from eventra_engagement.notification where user_id=? and resource_id=?",
            Integer.class,
            user,
            ticket.toString()));
  }
}

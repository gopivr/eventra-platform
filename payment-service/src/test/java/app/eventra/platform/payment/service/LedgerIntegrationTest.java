package app.eventra.platform.payment.service;

import static org.junit.jupiter.api.Assertions.*;

import app.eventra.platform.payment.domain.JournalEntry;
import app.eventra.platform.payment.domain.JournalLine;
import java.util.List;
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

@SpringBootTest(
    properties = {
      "spring.kafka.listener.auto-startup=false",
      "payment.reconciliation-delay=3600000"
    })
@Testcontainers(disabledWithoutDocker = true)
class LedgerIntegrationTest {
  @Container
  static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry r) {
    r.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    r.add("spring.datasource.username", POSTGRES::getUsername);
    r.add("spring.datasource.password", POSTGRES::getPassword);
  }

  @MockBean JwtDecoder jwtDecoder;
  @Autowired PaymentService payments;
  @Autowired JdbcTemplate jdbc;

  @Test
  void balancedEntryPersistsAgainstTheCreatedJournal() {
    UUID cash = UUID.fromString("00000000-0000-0000-0000-000000000201"),
        commission = UUID.fromString("00000000-0000-0000-0000-000000000202");
    JournalEntry entry =
        payments.post(
            "CAPTURE",
            "capture-1",
            List.of(
                new JournalLine(UUID.randomUUID(), UUID.randomUUID(), cash, 100, 0, "INR"),
                new JournalLine(UUID.randomUUID(), UUID.randomUUID(), commission, 0, 100, "INR")));
    assertEquals(
        2,
        jdbc.queryForObject(
            "select count(*) from eventra_payment.journal_line where journal_entry_id=?",
            Integer.class,
            entry.getId()));
    assertEquals(
        100L,
        jdbc.queryForObject(
            "select sum(debit_minor) from eventra_payment.journal_line where journal_entry_id=?",
            Long.class,
            entry.getId()));
    assertEquals(
        100L,
        jdbc.queryForObject(
            "select sum(credit_minor) from eventra_payment.journal_line where journal_entry_id=?",
            Long.class,
            entry.getId()));
  }

  @Test
  void unbalancedEntryRollsBack() {
    UUID cash = UUID.fromString("00000000-0000-0000-0000-000000000201");
    assertThrows(
        IllegalArgumentException.class,
        () ->
            payments.post(
                "CAPTURE",
                "bad",
                List.of(
                    new JournalLine(UUID.randomUUID(), UUID.randomUUID(), cash, 100, 0, "INR"))));
    assertEquals(
        0,
        jdbc.queryForObject(
            "select count(*) from eventra_payment.journal_entry where reference_id='bad'",
            Integer.class));
  }
}

package app.eventra.platform.booking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
    properties = {
      "spring.kafka.listener.auto-startup=false",
      "booking.hold-reconciliation-delay=3600000"
    })
@Testcontainers(disabledWithoutDocker = true)
class SeatHoldIntegrationTest {
  @Container
  static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

  @Container
  static final GenericContainer<?> REDIS =
      new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
    registry.add("spring.data.redis.host", REDIS::getHost);
    registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
  }

  @MockBean JwtDecoder jwtDecoder;
  @Autowired BookingService booking;
  @Autowired JdbcTemplate jdbc;
  private UUID seatId;

  @BeforeEach
  void createSeat() {
    UUID event = UUID.randomUUID();
    UUID map = UUID.randomUUID();
    UUID section = UUID.randomUUID();
    seatId = UUID.randomUUID();
    jdbc.update(
        "insert into eventra_booking.seat_map(id,event_id,name) values (?,?,?)",
        map,
        event,
        "Main");
    jdbc.update(
        "insert into eventra_booking.seat_section(id,seat_map_id,name) values (?,?,?)",
        section,
        map,
        "A");
    jdbc.update(
        "insert into eventra_booking.seat(id,section_id,seat_label) values (?,?,?)",
        seatId,
        section,
        "1");
  }

  @Test
  void twoConcurrentTransactionsProduceOneDurableHold() throws Exception {
    CountDownLatch start = new CountDownLatch(1);
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      var attempts =
          List.of(executor.submit(() -> attempt(start)), executor.submit(() -> attempt(start)));
      start.countDown();
      long successes = 0;
      for (var attempt : attempts) if (attempt.get()) successes++;
      assertEquals(1, successes);
      assertEquals(
          1,
          jdbc.queryForObject(
              "select count(*) from eventra_booking.seat_hold where status='ACTIVE'", Long.class));
      assertEquals(
          1,
          jdbc.queryForObject(
              "select count(*) from eventra_booking.seat_hold_item where seat_id=?",
              Long.class,
              seatId));
    }
  }

  private boolean attempt(CountDownLatch start) throws InterruptedException {
    start.await();
    try {
      booking.hold(UUID.randomUUID(), UUID.randomUUID(), List.of(seatId));
      return true;
    } catch (IllegalStateException unavailable) {
      return false;
    }
  }
}

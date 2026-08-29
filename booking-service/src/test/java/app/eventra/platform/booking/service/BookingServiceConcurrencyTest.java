package app.eventra.platform.booking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import app.eventra.platform.booking.domain.SeatHold;
import app.eventra.platform.booking.repository.AdmissionCredentialRepository;
import app.eventra.platform.booking.repository.BookingOutboxEventRepository;
import app.eventra.platform.booking.repository.CheckInRepository;
import app.eventra.platform.booking.repository.SeatHoldItemRepository;
import app.eventra.platform.booking.repository.SeatHoldRepository;
import app.eventra.platform.booking.repository.TicketOrderRepository;
import app.eventra.platform.booking.repository.TicketRepository;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

class BookingServiceConcurrencyTest {
  @Test
  void concurrentInstancesCannotHoldTheSameSeat() throws Exception {
    Map<String, String> redisState = new ConcurrentHashMap<>();
    StringRedisTemplate redis = mock(StringRedisTemplate.class);
    @SuppressWarnings("unchecked")
    ValueOperations<String, String> values = mock(ValueOperations.class);
    when(redis.opsForValue()).thenReturn(values);
    when(values.setIfAbsent(anyString(), anyString(), any(Duration.class)))
        .thenAnswer(
            call -> redisState.putIfAbsent(call.getArgument(0), call.getArgument(1)) == null);

    SeatHoldRepository holds = mock(SeatHoldRepository.class);
    when(holds.save(any(SeatHold.class))).thenAnswer(call -> call.getArgument(0));
    SeatHoldItemRepository items = mock(SeatHoldItemRepository.class);
    BookingService first = service(holds, items, redis);
    BookingService second = service(holds, items, redis);
    UUID seat = UUID.randomUUID();
    CountDownLatch start = new CountDownLatch(1);

    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      Future<Boolean> one = executor.submit(() -> attempt(first, start, seat));
      Future<Boolean> two = executor.submit(() -> attempt(second, start, seat));
      start.countDown();
      assertEquals(1, List.of(one.get(), two.get()).stream().filter(Boolean::booleanValue).count());
    }
  }

  @Test
  void duplicateSeatsAreRejectedBeforeRedisIsTouched() {
    BookingService service =
        service(
            mock(SeatHoldRepository.class),
            mock(SeatHoldItemRepository.class),
            mock(StringRedisTemplate.class));
    UUID seat = UUID.randomUUID();
    assertThrows(
        IllegalArgumentException.class,
        () -> service.hold(UUID.randomUUID(), UUID.randomUUID(), List.of(seat, seat)));
  }

  private boolean attempt(BookingService service, CountDownLatch start, UUID seat)
      throws InterruptedException {
    start.await();
    try {
      service.hold(UUID.randomUUID(), UUID.randomUUID(), List.of(seat));
      return true;
    } catch (IllegalStateException unavailable) {
      return false;
    }
  }

  private BookingService service(
      SeatHoldRepository holds, SeatHoldItemRepository items, StringRedisTemplate redis) {
    return new BookingService(
        holds,
        items,
        mock(TicketOrderRepository.class),
        mock(TicketRepository.class),
        mock(CheckInRepository.class),
        mock(BookingOutboxEventRepository.class),
        mock(AdmissionCredentialRepository.class),
        redis);
  }
}

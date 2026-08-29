package app.eventra.platform.booking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import app.eventra.platform.booking.domain.AdmissionCredential;
import app.eventra.platform.booking.domain.CheckIn;
import app.eventra.platform.booking.domain.Ticket;
import app.eventra.platform.booking.domain.TicketOrder;
import app.eventra.platform.booking.repository.AdmissionCredentialRepository;
import app.eventra.platform.booking.repository.BookingOutboxEventRepository;
import app.eventra.platform.booking.repository.CheckInRepository;
import app.eventra.platform.booking.repository.SeatHoldItemRepository;
import app.eventra.platform.booking.repository.SeatHoldRepository;
import app.eventra.platform.booking.repository.TicketOrderRepository;
import app.eventra.platform.booking.repository.TicketRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;

class AdmissionValidationTest {
  @Test
  void credentialMustBelongToTheScannedEvent() throws Exception {
    UUID eventId = UUID.randomUUID();
    UUID orderId = UUID.randomUUID();
    UUID ticketId = UUID.randomUUID();
    String raw = "one-time-qr-secret";
    Ticket ticket = new Ticket(ticketId, orderId, UUID.randomUUID());
    TicketOrder order =
        new TicketOrder(orderId, UUID.randomUUID(), eventId, "key", "USD", 10, 0, 0, "{}");
    order.confirm();

    AdmissionCredentialRepository credentials = mock(AdmissionCredentialRepository.class);
    when(credentials.findByTokenHash(sha256(raw)))
        .thenReturn(Optional.of(new AdmissionCredential(ticketId, sha256(raw))));
    TicketRepository tickets = mock(TicketRepository.class);
    when(tickets.findById(ticketId)).thenReturn(Optional.of(ticket));
    when(tickets.save(any(Ticket.class))).thenAnswer(call -> call.getArgument(0));
    TicketOrderRepository orders = mock(TicketOrderRepository.class);
    when(orders.findById(orderId)).thenReturn(Optional.of(order));
    CheckInRepository checkIns = mock(CheckInRepository.class);
    when(checkIns.findByTicketId(ticketId)).thenReturn(Optional.empty());
    when(checkIns.save(any(CheckIn.class))).thenAnswer(call -> call.getArgument(0));
    BookingService service =
        new BookingService(
            mock(SeatHoldRepository.class),
            mock(SeatHoldItemRepository.class),
            orders,
            tickets,
            checkIns,
            mock(BookingOutboxEventRepository.class),
            credentials,
            mock(StringRedisTemplate.class));

    CheckIn checkedIn = service.checkIn(eventId, raw, "scanner-subject");
    assertEquals(ticketId, checkedIn.getTicketId());
    assertEquals("CHECKED_IN", ticket.getStatus());
    assertThrows(
        IllegalArgumentException.class,
        () -> service.checkIn(UUID.randomUUID(), raw, "scanner-subject"));
    assertThrows(
        IllegalArgumentException.class, () -> service.checkIn(eventId, "wrong", "scanner-subject"));
  }

  private String sha256(String value) throws Exception {
    return HexFormat.of()
        .formatHex(
            MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
  }
}

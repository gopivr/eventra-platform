package app.eventra.platform.booking.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class BookingDomainTest {
  @Test
  void orderCalculatesAuthoritativeTotal() {
    TicketOrder order =
        new TicketOrder(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            "key",
            "INR",
            100,
            18,
            2,
            "{}");
    assertEquals(120, order.getTotalMinor());
  }

  @Test
  void orderRejectsNegativeAmounts() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new TicketOrder(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "key",
                "INR",
                -1,
                0,
                0,
                "{}"));
  }

  @Test
  void ticketAssignmentRequiresAnIdentity() {
    Ticket ticket = new Ticket(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    assertThrows(IllegalArgumentException.class, () -> ticket.assign(null, "  "));
    UUID assignee = UUID.randomUUID();
    ticket.assign(assignee, " Guest ");
    assertEquals(assignee, ticket.getAssigneeUserId());
    assertEquals("Guest", ticket.getAssigneeName());
  }

  @Test
  void checkedInTicketCannotBeCheckedInTwice() {
    Ticket ticket = new Ticket(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    ticket.checkIn();
    assertEquals("CHECKED_IN", ticket.getStatus());
    assertThrows(IllegalStateException.class, ticket::checkIn);
  }
}

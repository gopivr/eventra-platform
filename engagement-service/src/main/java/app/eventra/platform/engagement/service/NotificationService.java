package app.eventra.platform.engagement.service;

import app.eventra.platform.engagement.domain.InboxMessage;
import app.eventra.platform.engagement.domain.Notification;
import app.eventra.platform.engagement.repository.InboxMessageRepository;
import app.eventra.platform.engagement.repository.NotificationRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {
  private final NotificationRepository notifications;
  private final InboxMessageRepository inbox;

  public NotificationService(NotificationRepository notifications, InboxMessageRepository inbox) {
    this.notifications = notifications;
    this.inbox = inbox;
  }

  @Transactional
  public void paymentCaptured(String messageId, UUID userId, String orderId) {
    InboxMessage.Key key = new InboxMessage.Key(messageId, "engagement-payment-captured");
    if (inbox.existsById(key)) return;
    inbox.save(new InboxMessage(messageId, "engagement-payment-captured"));
    notifications.save(
        new Notification(
            UUID.randomUUID(),
            userId,
            "TICKET_CONFIRMATION",
            "Payment received",
            "Your ticket order has been confirmed.",
            "ticket-order",
            orderId));
  }

  @Transactional
  public void ticketIssued(String messageId, UUID userId, String orderId, UUID ticketId) {
    InboxMessage.Key key = new InboxMessage.Key(messageId, "engagement-ticket-issued");
    if (inbox.existsById(key)) return;
    inbox.save(new InboxMessage(messageId, "engagement-ticket-issued"));
    notifications.save(
        new Notification(
            UUID.randomUUID(),
            userId,
            "TICKET_ISSUED",
            "Ticket ready",
            "Your admission ticket is ready.",
            "ticket",
            ticketId.toString()));
  }

  public List<Notification> list(UUID user) {
    return notifications.findByUserIdOrderByCreatedAtDesc(user);
  }

  @Transactional
  public void markRead(UUID id, UUID user) {
    notifications
        .findByIdAndUserId(id, user)
        .orElseThrow(
            () ->
                new org.springframework.security.access.AccessDeniedException(
                    "notification is owned by another user"))
        .markRead();
  }
}

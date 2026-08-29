package app.eventra.platform.engagement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "inbox_message", schema = "eventra_engagement")
@IdClass(InboxMessage.Key.class)
public class InboxMessage {
  @Id
  @Column(name = "message_id")
  private String messageId;

  @Id
  @Column(name = "consumer_name")
  private String consumerName;

  @Column(name = "received_at", nullable = false)
  private Instant receivedAt;

  @Column(nullable = false)
  private String status;

  protected InboxMessage() {}

  public InboxMessage(String id, String consumer) {
    messageId = id;
    consumerName = consumer;
    receivedAt = Instant.now();
    status = "PROCESSED";
  }

  public record Key(String messageId, String consumerName) {}
}

package app.eventra.platform.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ticket", schema = "eventra_booking")
public class Ticket {
  @Id private UUID id;

  @Column(name = "order_id", nullable = false)
  private UUID orderId;

  @Column(name = "order_item_id")
  private UUID orderItemId;

  @Column(nullable = false)
  private String status = "ISSUED";

  @Column(name = "issued_at", nullable = false)
  private Instant issuedAt;

  @Column(name = "assignee_user_id")
  private UUID assigneeUserId;

  @Column(name = "assignee_name")
  private String assigneeName;

  protected Ticket() {}

  public Ticket(UUID id, UUID order, UUID item) {
    this.id = id;
    orderId = order;
    orderItemId = item;
    issuedAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public UUID getOrderId() {
    return orderId;
  }

  public String getStatus() {
    return status;
  }

  public UUID getAssigneeUserId() {
    return assigneeUserId;
  }

  public String getAssigneeName() {
    return assigneeName;
  }

  public void assign(UUID user, String name) {
    if (user == null && (name == null || name.isBlank()))
      throw new IllegalArgumentException("an assignee user or name is required");
    assigneeUserId = user;
    assigneeName = name == null ? null : name.trim();
  }

  public void checkIn() {
    if (!"ISSUED".equals(status))
      throw new IllegalStateException("ticket is not valid for check-in");
    status = "CHECKED_IN";
  }
}

package app.eventra.platform.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "seat_hold_item", schema = "eventra_booking")
@IdClass(SeatHoldItem.Key.class)
public class SeatHoldItem {
  @Id
  @Column(name = "hold_id")
  private UUID holdId;

  @Id
  @Column(name = "seat_id")
  private UUID seatId;

  protected SeatHoldItem() {}

  public SeatHoldItem(UUID holdId, UUID seatId) {
    this.holdId = holdId;
    this.seatId = seatId;
  }

  public UUID getSeatId() {
    return seatId;
  }

  public record Key(UUID holdId, UUID seatId) {}
}

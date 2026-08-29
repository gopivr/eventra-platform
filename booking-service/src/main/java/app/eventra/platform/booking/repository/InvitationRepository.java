package app.eventra.platform.booking.repository;

import app.eventra.platform.booking.domain.Invitation;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationRepository extends JpaRepository<Invitation, UUID> {}

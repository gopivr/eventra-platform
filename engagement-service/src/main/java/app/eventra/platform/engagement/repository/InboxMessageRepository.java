package app.eventra.platform.engagement.repository;

import app.eventra.platform.engagement.domain.InboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxMessageRepository extends JpaRepository<InboxMessage, InboxMessage.Key> {}

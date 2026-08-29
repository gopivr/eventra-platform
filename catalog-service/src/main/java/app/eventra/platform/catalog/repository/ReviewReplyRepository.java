package app.eventra.platform.catalog.repository;

import app.eventra.platform.catalog.domain.ReviewReply;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewReplyRepository extends JpaRepository<ReviewReply, UUID> {}

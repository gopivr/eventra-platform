package app.eventra.platform.catalog.repository;

import app.eventra.platform.catalog.domain.Review;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
  List<Review> findByProviderIdAndModerationState(UUID providerId, String state);
}

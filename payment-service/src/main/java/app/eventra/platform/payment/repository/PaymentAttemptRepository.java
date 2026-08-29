package app.eventra.platform.payment.repository;

import app.eventra.platform.payment.domain.PaymentAttempt;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentAttemptRepository extends JpaRepository<PaymentAttempt, UUID> {}

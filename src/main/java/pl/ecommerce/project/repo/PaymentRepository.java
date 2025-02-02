package pl.ecommerce.project.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.project.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}

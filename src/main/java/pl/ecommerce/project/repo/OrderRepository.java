package pl.ecommerce.project.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.project.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}

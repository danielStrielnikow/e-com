package pl.ecommerce.project.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.project.model.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}

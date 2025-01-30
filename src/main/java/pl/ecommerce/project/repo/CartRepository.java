package pl.ecommerce.project.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.project.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
}

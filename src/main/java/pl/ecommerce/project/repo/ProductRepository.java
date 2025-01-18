package pl.ecommerce.project.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.project.model.Product;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}

package pl.ecommerce.project.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.project.model.Category;
import pl.ecommerce.project.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategoryOrderByPriceAsc(Category category, Pageable pageDetails);
    Page<Product> findByProductNameLikeIgnoreCase(String keyword, Pageable pageDetails);
}

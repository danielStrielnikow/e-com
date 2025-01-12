package pl.ecommerce.project.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.ecommerce.project.model.Category;



public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByCategoryName(String categoryName);
}

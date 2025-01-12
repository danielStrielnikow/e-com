package pl.ecommerce.project.service;

import org.springframework.stereotype.Service;
import pl.ecommerce.project.exception.APIException;
import pl.ecommerce.project.exception.ResourceNotFoundException;
import pl.ecommerce.project.model.Category;
import pl.ecommerce.project.repo.CategoryRepository;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new APIException("No category created till now");
        }
        return categories;
    }

    public void createCategory(Category category) {
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if (savedCategory != null) {
            throw new APIException("Category with the name " + category.getCategoryName() + " already exists!!!");
        }
        categoryRepository.save(category);
    }

    public void deleteCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        categoryRepository.delete(category);
    }


    public void updateCategory(Category category, Long categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        category.setCategoryId(categoryId);
        categoryRepository.save(category);
    }
}

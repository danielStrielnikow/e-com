package pl.ecommerce.project.service;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.ecommerce.project.config.AppErrors;
import pl.ecommerce.project.exception.APIException;
import pl.ecommerce.project.exception.ResourceNotFoundException;
import pl.ecommerce.project.model.Category;
import pl.ecommerce.project.payload.CategoryResponse;
import pl.ecommerce.project.payload.dto.CategoryDTO;
import pl.ecommerce.project.repo.CategoryRepository;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryService(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageable = PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by(Sort.Direction.fromString(sortOrder), sortBy)
        );

        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        return getCategoryResponse(categoryPage);
    }

    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        if (categoryRepository.findByCategoryName(categoryDTO.getCategoryName()) != null) {
            throw new APIException(AppErrors.ERROR_CATEGORY_EXISTS);
        }

        Category savedCategory = categoryRepository.save(mapToEntity(categoryDTO));
        return mapToDTO(savedCategory);
    }

    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category existingCategory = fetchCategoryById(categoryId);

        existingCategory.setCategoryName(categoryDTO.getCategoryName());
        Category updatedCategory = categoryRepository.save(existingCategory);

        return mapToDTO(updatedCategory);
    }

    public CategoryDTO deleteCategoryById(Long categoryId) {
        Category existingCategory = fetchCategoryById(categoryId);
        categoryRepository.delete(existingCategory);
        return mapToDTO(existingCategory);
    }
    private CategoryResponse getCategoryResponse(Page<Category> categoryPage) {
        if (categoryPage.isEmpty()) {
            throw new APIException(AppErrors.ERROR_CATEGORY_NOT_FOUND);
        }

        List<CategoryDTO> categoryDTOS = categoryPage.getContent()
                .stream()
                .map(this::mapToDTO)
                .toList();

        return new CategoryResponse(
                categoryDTOS,
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages(),
                categoryPage.isLast()
        );
    }


    private Category fetchCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
    }

    private CategoryDTO mapToDTO(Category category) {
        return modelMapper.map(category, CategoryDTO.class);
    }

    private Category mapToEntity(CategoryDTO categoryDTO) {
        return modelMapper.map(categoryDTO, Category.class);
    }
}

package pl.ecommerce.project.service;

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
import pl.ecommerce.project.payload.dto.DTOMapper;
import pl.ecommerce.project.repo.CategoryRepository;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final DTOMapper dtoMapper;

    public CategoryService(CategoryRepository categoryRepository, DTOMapper dtoMapper) {
        this.categoryRepository = categoryRepository;
        this.dtoMapper = dtoMapper;
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

        Category savedCategory = categoryRepository.save(dtoMapper.mapCategoryToEntity(categoryDTO));
        return dtoMapper.mapToCategoryDTO(savedCategory);
    }

    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category existingCategory = fetchCategoryById(categoryId);

        existingCategory.setCategoryName(categoryDTO.getCategoryName());
        Category updatedCategory = categoryRepository.save(existingCategory);

        return dtoMapper.mapToCategoryDTO(updatedCategory);
    }

    public CategoryDTO deleteCategoryById(Long categoryId) {
        Category existingCategory = fetchCategoryById(categoryId);
        categoryRepository.delete(existingCategory);
        return dtoMapper.mapToCategoryDTO(existingCategory);
    }
    private CategoryResponse getCategoryResponse(Page<Category> categoryPage) {
        if (categoryPage.isEmpty()) {
            throw new APIException(AppErrors.ERROR_CATEGORY_NOT_FOUND);
        }

        List<CategoryDTO> categoryDTOS = categoryPage.getContent()
                .stream()
                .map(dtoMapper::mapToCategoryDTO)
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
}


package pl.ecommerce.project.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.ecommerce.project.payload.CategoryDTO;
import pl.ecommerce.project.payload.CategoryResponse;
import pl.ecommerce.project.service.CategoryService;

@RestController
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber") Integer pageNumber,
            @RequestParam(name = "pageSize") Integer pageSize) {

        CategoryResponse allCategories = categoryService.getAllCategories(pageNumber, pageSize);
        return ResponseEntity.ok(allCategories);
    }

    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategoryDTO = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);
    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO,
                                                 @PathVariable Long categoryId) {
        CategoryDTO updateCategoryDTO = categoryService.updateCategory(categoryDTO, categoryId);
        return ResponseEntity.ok(updateCategoryDTO);

    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategoryById(@PathVariable Long categoryId) {
        CategoryDTO deletedCategoryDTO = categoryService.deleteCategoryById(categoryId);
        return ResponseEntity.ok(deletedCategoryDTO);

    }

}

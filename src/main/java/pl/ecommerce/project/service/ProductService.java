package pl.ecommerce.project.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.ecommerce.project.config.AppErrors;
import pl.ecommerce.project.exception.APIException;
import pl.ecommerce.project.exception.ResourceNotFoundException;
import pl.ecommerce.project.model.Cart;
import pl.ecommerce.project.model.Category;
import pl.ecommerce.project.model.Product;
import pl.ecommerce.project.payload.ProductResponse;
import pl.ecommerce.project.payload.dto.CartDTO;
import pl.ecommerce.project.payload.dto.ProductDTO;
import pl.ecommerce.project.repo.CartRepository;
import pl.ecommerce.project.repo.CategoryRepository;
import pl.ecommerce.project.repo.ProductRepository;
import pl.ecommerce.project.service.fileService.FileServiceImpl;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final FileServiceImpl fileService;
    private final CartRepository cartRepository;
    private final CartService cartService;

    @Value("${project.image}")
    private String imagePath;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          ModelMapper modelMapper,
                          FileServiceImpl fileService, CartRepository cartRepository, CartService cartService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.fileService = fileService;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
    }

    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageDetails = getPageDetails(pageNumber, pageSize, sortBy, sortOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);

        return mapToProductResponse(productPage);
    }

    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = fetchCategoryById(categoryId);

        Pageable pageDetails = getPageDetails(pageNumber, pageSize, sortBy, sortOrder);
        Page<Product> productPage = productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);

        List<Product> product = productPage.getContent();
        if (product.isEmpty()) {
            throw new APIException(category.getCategoryName()+ " " + AppErrors.ERROR_CATEGORY_NO_PRODUCTS);
        } else {
            return mapToProductResponse(productPage);
        }

    }

    public ProductResponse searchProductByKeyWord(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageDetails = getPageDetails(pageNumber, pageSize, sortBy, sortOrder);
        Page<Product> productPage = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageDetails);

        return mapToProductResponse(productPage);
    }

    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category category = fetchCategoryById(categoryId);

        boolean productExists = category.getProducts().stream()
                .anyMatch(product -> product.getProductName().equalsIgnoreCase(productDTO.getProductName()));
        if (productExists) throw new APIException(AppErrors.ERROR_PRODUCT_EXISTS);

        Product product = modelMapper.map(productDTO, Product.class);
        product.setImage(AppErrors.DEFAULT_IMAGE);
        product.setCategory(category);
        product.setSpecialPrice(calculateSpecialPrice(product.getPrice(), product.getDiscount()));

        Product savedProduct = productRepository.save(product);
        return mapToProductDTO(savedProduct);
    }

    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product product = fetchProductById(productId);
        modelMapper.map(productDTO, product);
        product.setSpecialPrice(calculateSpecialPrice(product.getPrice(), product.getDiscount()));

        savedProductToDTO(productDTO, product);
        Product updatedProduct = productRepository.save(product);

        List<Cart> carts = cartRepository.findCartByProductId(productId);

        List<CartDTO> cartDTOS = carts.stream()
                .map(cart -> {
                    CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

                    List<ProductDTO> products = cart.getCartItems().stream()
                            .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class))
                            .toList();
                    cartDTO.setProducts(products);

                    return cartDTO;
                }).toList();

        cartDTOS.forEach(cart -> cartService.updateProductInCart(cart.getCartId(), productId));

        return mapToProductDTO(updatedProduct);
    }

    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product product = fetchProductById(productId);
        validateImageFile(image);

        String fileName;
        try {
            fileName = fileService.uploadImage(imagePath, image);
        } catch (IOException e) {
            throw new IOException("Error uploading image", e);
        }

        product.setImage(fileName);
        Product updatedProduct = productRepository.save(product);
        return mapToProductDTO(updatedProduct);
    }

    public ProductDTO deleteProductById(Long productId) {
        Product product = fetchProductById(productId);

        List<Cart> carts = cartRepository.findCartByProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));

        productRepository.delete(product);
        return mapToProductDTO(product);
    }

    private static void savedProductToDTO(ProductDTO productDTO, Product product) {
        product.setProductName(productDTO.getProductName());
        product.setDescription(productDTO.getDescription());
        product.setQuantity(productDTO.getQuantity());
        product.setDiscount(productDTO.getDiscount());
        product.setPrice(productDTO.getPrice());
        product.setSpecialPrice(productDTO.getSpecialPrice());
    }

    // Helper methods

    private ProductResponse mapToProductResponse(Page<Product> productPage) {
        if (productPage.isEmpty()) throw new APIException(AppErrors.ERROR_NO_PRODUCTS);

        List<ProductDTO> productDTOS = productPage.getContent().stream()
                .map(this::mapToProductDTO)
                .toList();
        return new ProductResponse(
                productDTOS,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast());
    }

    private Product fetchProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
    }

    private Category fetchCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
    }

    private ProductDTO mapToProductDTO(Product product) {
        return modelMapper.map(product, ProductDTO.class);
    }

    private double calculateSpecialPrice(double price, double discount) {
        return price - (discount / 100 * price);
    }

    private static Pageable getPageDetails(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        if (pageNumber < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Page number must be non-negative and page size must be greater than 0");
        }

        Sort sortByAndOrder;
        try {
            sortByAndOrder = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid sortOrder: " + sortOrder);
        }

        return PageRequest.of(pageNumber, pageSize, sortByAndOrder);
    }

    private void validateImageFile(MultipartFile image) {
        if (image.isEmpty() || !image.getContentType().startsWith("image/")) {
            throw new APIException("Invalid image file");
        }
    }
}

package pl.ecommerce.project.payload.dto;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.ecommerce.project.model.*;

import java.util.List;
@Component
public class DTOMapper {
    private final ModelMapper modelMapper;

    public DTOMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public CartDTO convertToCartDTO(Cart cart) {
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<ProductDTO> products = cart.getCartItems().stream()
                .map(item -> {
                    ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
                    productDTO.setQuantity(item.getQuantity());
                    return productDTO;
                }).toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }


    public CartDTO mapToCartDTO(Cart cart) {
        return modelMapper.map(cart, CartDTO.class);
    }

    public Category mapCategoryToEntity(CategoryDTO categoryDTO) {
        return modelMapper.map(categoryDTO, Category.class);
    }
    public CategoryDTO mapToCategoryDTO(Category category) {
        return modelMapper.map(category, CategoryDTO.class);
    }

    public Product mapProductToEntity(ProductDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }
    public ProductDTO mapToProductDTO(Product product) {
        return modelMapper.map(product, ProductDTO.class);
    }

    public Address mapAddressToEntity(AddressDTO addressDTO) {
        return modelMapper.map(addressDTO, Address.class);
    }
    public AddressDTO mapToAddressDTO(Address address) {
        return modelMapper.map(address, AddressDTO.class);
    }

    public OrderDTO mapToOrderDTO(Order order) {
        return modelMapper.map(order, OrderDTO.class);
    }

    public OrderItemDTO mapToOrderItemDTO(OrderItem orderItem) {
        return modelMapper.map(orderItem, OrderItemDTO.class);
    }
}


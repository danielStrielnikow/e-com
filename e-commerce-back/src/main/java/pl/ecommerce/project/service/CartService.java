package pl.ecommerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.ecommerce.project.exception.APIException;
import pl.ecommerce.project.exception.ResourceNotFoundException;
import pl.ecommerce.project.model.Cart;
import pl.ecommerce.project.model.CartItem;
import pl.ecommerce.project.model.Product;
import pl.ecommerce.project.payload.dto.CartDTO;
import pl.ecommerce.project.payload.dto.CartItemDTO;
import pl.ecommerce.project.payload.dto.DTOMapper;
import pl.ecommerce.project.payload.dto.ProductDTO;
import pl.ecommerce.project.repo.CartItemRepository;
import pl.ecommerce.project.repo.CartRepository;
import pl.ecommerce.project.repo.ProductRepository;
import pl.ecommerce.project.util.AuthUtil;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final AuthUtil authUtil;
    private final DTOMapper dtoMapper;

    private final double INITIAL_NUMBER = 0.00;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       AuthUtil authUtils,
                       DTOMapper dtoMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.authUtil = authUtils;
        this.dtoMapper = dtoMapper;
    }

    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart = getOrCreateCart();

        Product product = fetchProductById(productId);

        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(cart.getCartId(), productId);

        if (cartItem != null) {
            throw new APIException("Product " + product.getProductName() + " already exists in the cart");
        }

        validateProductAvailability(product, quantity);

        CartItem newCartItem = new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);

        product.setQuantity(product.getQuantity());

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

        cartRepository.save(cart);

        return dtoMapper.convertToCartDTO(cart);
    }

    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();

        if (carts.isEmpty()) {
            throw new APIException("No cart exists");
        }


        return carts.stream().map(cart -> {
            CartDTO cartDTO = dtoMapper.mapToCartDTO(cart);

            List<ProductDTO> products = cart.getCartItems().stream().map(cartItem -> {
                ProductDTO productDTO = dtoMapper.mapToProductDTO(cartItem.getProduct());
                productDTO.setQuantity(cartItem.getQuantity()); // Set the quantity from CartItem
                return productDTO;
            }).toList();


            cartDTO.setProducts(products);

            return cartDTO;
        }).toList();
    }

    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }

        CartDTO cartDTO = dtoMapper.mapToCartDTO(cart);
        cart.getCartItems().forEach(c -> c.getProduct().setQuantity(c.getQuantity()));
        List<ProductDTO> productDTOS = cart.getCartItems().stream()
                .map(p -> dtoMapper.mapToProductDTO(p.getProduct()))
                .toList();
        cartDTO.setProducts(productDTOS);
        return cartDTO;
    }

    @Transactional
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        Long cartId = userCart.getCartId();

        Cart cart = fetchCartById(cartId);

        Product product = fetchProductById(productId);

        validateProductAvailability(product, quantity);

        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(cartId, productId);

        if (cartItem == null) {
            throw new APIException("Product" + product.getProductName() + " not available in the cart!");
        }

        // Calculate new quantity
        int newQuantity = cartItem.getQuantity() + quantity;
        // Validation to prevent negative quantities
        if (newQuantity < 0) {
            throw new APIException("The resulting quantity cannot be negative");
        }

        if (newQuantity == 0) {
            deleteProductFromCart(cartId, productId);
        } else {
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
            cartRepository.save(cart);
        }

        CartItem updateItem = cartItemRepository.save(cartItem);
        if (updateItem.getQuantity() == 0) {
            cartItemRepository.deleteById(updateItem.getCartItemId());
        }

        return dtoMapper.convertToCartDTO(cart);
    }

    @Transactional
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = fetchCartById(cartId);

        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(cartId, productId);

        if (cartItem == null) {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        cart.setTotalPrice(cart.getTotalPrice() -
                (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByCartIdAndProductId(cartId, productId);

        return "Product " + cartItem.getProduct().getProductName() + " removed from the cart!";
    }

    public void updateProductInCart(Long cartId, Long productId) {
        Product product = fetchProductById(productId);

        Cart cart = fetchCartById(cartId);

        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(cartId, productId);

        if (cartItem == null) {
            throw new APIException("Product" + product.getProductName() + " not available in the cart!");
        }

        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

        cartItem.setProductPrice(product.getSpecialPrice());

        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.save(cartItem);
    }

    @Transactional
    public String createOrUpdateCartWithItems(List<CartItemDTO> cartItems) {
        // Get user's email
        String emailId = authUtil.loggedInEmail();

        // Check if an existing cart is available or create a new one
        Cart existingCart = cartRepository.findCartByEmail(emailId);
        if (existingCart == null) {
            existingCart = new Cart();
            existingCart.setTotalPrice(INITIAL_NUMBER);
            existingCart.setUser(authUtil.loggedInUser());
            existingCart = cartRepository.save(existingCart);
        } else {
            // Clear all current items in the existing cart
            cartItemRepository.deleteAllByCartId(existingCart.getCartId());
        }

        double totalPrice = INITIAL_NUMBER;

        // Process each item in the request to add to the cart
        for (CartItemDTO cartItemDTO : cartItems) {
            Long productId = cartItemDTO.getProductId();
            Integer quantity = cartItemDTO.getQuantity();

            // Find the product by ID
            Product product = fetchProductById(productId);

            // Directly update product stock and total price
            // product.setQuantity(product.getQuantity() - quantity);
            totalPrice += product.getSpecialPrice() * quantity;

            // Create and save cart item
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(existingCart);
            cartItem.setQuantity(quantity);
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setDiscount(product.getDiscount());
            cartItemRepository.save(cartItem);
        }

        // Update the cart's total price and save
        existingCart.setTotalPrice(totalPrice);
        cartRepository.save(existingCart);
        return "Cart created/updated with the new items successfully";
    }

    private void validateProductAvailability(Product product, int requestedQuantity) {
        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }
        if (product.getQuantity() < requestedQuantity) {
            throw new APIException("Please, order " + product.getProductName() +
                    " in a quantity less than or equal to " + product.getQuantity());
        }
    }


    private Cart getOrCreateCart() {
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if (userCart != null) {
            return userCart;
        }

        Cart cart = new Cart();
        cart.setTotalPrice(INITIAL_NUMBER);
        cart.setUser(authUtil.loggedInUser());

        return cartRepository.save(cart);
    }

    private Product fetchProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
    }

    private Cart fetchCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
    }
}

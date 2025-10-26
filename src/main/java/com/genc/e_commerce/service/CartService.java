package com.genc.e_commerce.service;

import com.genc.e_commerce.dto.CartRequest;
import com.genc.e_commerce.dto.CartResponse;
import com.genc.e_commerce.entity.Cart;
import com.genc.e_commerce.entity.Product;
import com.genc.e_commerce.entity.User;
import com.genc.e_commerce.exception.ResourceNotFoundException;
import com.genc.e_commerce.repository.CartRepo;
import com.genc.e_commerce.repository.ProductRepo;
import com.genc.e_commerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// @Slf4j is a Lombok annotation that automatically generates a logger field.
@Slf4j
// @Service marks this class as a Spring service component for business logic.
@Service
public class CartService {
    // @Autowired injects the CartRepo bean for database operations on the cart.
    @Autowired
    private CartRepo cartRepo;
    // @Autowired injects the UserRepository bean to fetch user data.
    @Autowired
    private UserRepository userRepository;
    // @Autowired injects the ProductRepo bean to fetch product data.
    @Autowired
    private ProductRepo productRepo;


    /**
     * Adds a product to a user's cart. If the product is already in the cart,
     * it updates the quantity and total price. Otherwise, it creates a new cart item.
     * @param cartRequest A DTO containing userId, productId, and quantity.
     * @return The saved or updated Cart entity.
     */
    // @Transactional ensures that all database operations in this method are atomic.
    @Transactional
    public Cart addToCart(CartRequest cartRequest) {
        log.info("Attempting to add product to cart for user ID:{} and product ID:{}",
                cartRequest.getUserId(), cartRequest.getProductId());

        // Fetches the user from the database or throws an exception if not found.
        User user = userRepository.findById(cartRequest.getUserId()).
                orElseThrow(() -> new ResourceNotFoundException("User not found"));
        log.debug("User found: {}", user.getUsername());

        // Fetches the product from the database or throws an exception if not found.
        Product product = productRepo.findById(cartRequest.getProductId()).
                orElseThrow(() -> new ResourceNotFoundException("Prodcut not found"));
        log.debug("Product found: {}", product.getName());

        // Checks if a cart item already exists for this specific user and product.
        Optional<Cart> existingCartItem = cartRepo.findByUserAndProduct(user, product);
        double unitprice = product.getPrice();

        // If the item is already in the cart, update the existing entry.
        if (existingCartItem.isPresent()) {
            Cart cart = existingCartItem.get();
            // Add the new quantity to the existing quantity.
            int updateQuantity = cart.getQuantity() + cartRequest.getQuantity();
            cart.setQuantity(updateQuantity);
            // Recalculate the total price for this item.
            cart.setItemPriceTotal(unitprice * updateQuantity);
            log.info("Existing cart item updated. New Quantity: {}", updateQuantity);
            // Save the updated cart item.
            return cartRepo.save(cart);
        }
        // If the item is not in the cart, create a new entry.
        else {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setProduct(product);
            cart.setQuantity(cartRequest.getQuantity());
            // Calculate the total price for the new item.
            cart.setItemPriceTotal(unitprice * cartRequest.getQuantity());
            log.info("New cart item created with Quantity: {}", cartRequest.getQuantity());
            // Save the new cart item.
            return cartRepo.save(cart);
        }
    }


    /**
     * Removes an item from the cart based on its cart ID.
     * @param cartId The unique ID of the cart item to remove.
     * @return An Optional containing the removed Cart item if it existed, otherwise an empty Optional.
     */
    @Transactional
    public Optional<Cart> removeFromCart(Long cartId) {
        log.info("Attempting to find and remove cart item using cart ID:{}", cartId);

        // Find the cart item by its primary key.
        Optional<Cart> existingCart = cartRepo.findById(cartId);

        // If the cart item exists...
        if (existingCart.isPresent()) {
            // ...delete it from the database.
            cartRepo.deleteById(cartId);
            log.info("Cart item successfully deleted: {}", cartId);
            // Return the item that was just deleted.
            return existingCart;
        }

        // If the item was not found, log a warning and return an empty result.
        log.warn("Cart item not found for removal: {}", cartId);
        return Optional.empty();
    }

    /**
     * Retrieves all items in a user's cart and maps them to a response DTO.
     * @param userId The ID of the user whose cart details are being requested.
     * @return A list of CartResponse objects representing the items in the cart.
     */
    public List<CartResponse> getCartDetails(Long userId) {
        // First, ensure the user exists, otherwise throw an exception.
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("No user found"));

        // Find all cart items associated with the given user ID.
        List<Cart> cartItem = cartRepo.findByUserUserId(userId);

        // Convert the list of Cart entities into a list of CartResponse DTOs.
        return cartItem.stream()
                .map(CartResponse::new) // Uses the CartResponse(Cart cart) constructor
                .collect(Collectors.toList());
    }

    /**
     * Updates the quantity of a specific item within the cart.
     * @param cartId The ID of the cart item to update.
     * @param newQuantity The new quantity for the item (must be 1 or greater).
     * @return The updated Cart entity.
     */
    @Transactional
    public Cart updateQuantity(Long cartId, int newQuantity) {

        // Validate that the new quantity is a positive number.
        if (newQuantity < 1) {
            throw new IllegalArgumentException("quantity must not be less than 1");
        }
        // Find the cart item by its ID or throw an exception if not found.
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new RuntimeException("No cart item found with the cart ID " + cartId));

        // Get the unit price from the associated product.
        double unitprice = cart.getProduct().getPrice();
        // Set the new quantity.
        cart.setQuantity(newQuantity);

        // Recalculate the total price based on the new quantity.
        double newItemPriceTotal = unitprice * newQuantity;
        cart.setItemPriceTotal(newItemPriceTotal);

        log.info("Cart item ID {} quantity updated to {}. New Total: ${}", cartId, newQuantity, newItemPriceTotal);

        // Save the changes to the database and return the updated item.
        return cartRepo.save(cart);
    }
}
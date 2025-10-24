package com.genc.e_commerce.controller;

import com.genc.e_commerce.dto.CartRequest;
import com.genc.e_commerce.dto.CartResponse;
import com.genc.e_commerce.entity.Cart;
import com.genc.e_commerce.exception.ResourceNotFoundException;
import com.genc.e_commerce.service.CartService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CartController {
    private static final Logger logger = LogManager.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    @PostMapping("/add-product-to-cart")
    public ResponseEntity<?> addToCart(@Valid @RequestBody CartRequest cartRequest) {
        logger.debug("Request received to add product to cart: {}", cartRequest);
        Map<String, Object> response = new HashMap<>();
        try {
            Cart cart = cartService.addToCart(cartRequest);
            CartResponse cartResponse = new CartResponse(cart);
            response.put("message", "product added in the cart");
            response.put("data", cartResponse);

            logger.info("Product {} added to cart for user {}.", cartRequest.getProductId(), cartRequest.getUserId());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error occurred while adding product to cart for request: {}", cartRequest, e);
            response.put("message", "an error occured while adding product in cart");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/delete-cart-byId/{cartId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long cartId) {
        logger.info("Request received to remove cart item with ID: {}", cartId);
        Map<String, Object> response = new HashMap<>();

        Optional<Cart> deleteCart = cartService.removeFromCart(cartId);

        if (deleteCart.isPresent()) {
            Cart cart = deleteCart.get();
            CartResponse cartResponse = new CartResponse(cart);
            response.put("message", "cart item deleted successfully");
            response.put("data", cartResponse);
            logger.info("Successfully deleted cart item with ID: {}", cartId);
            return ResponseEntity.ok(response);
        } else {
            logger.warn("Attempted to delete a non-existent cart item with ID: {}", cartId);
            response.put("error", "cart item with cart id " + cartId + " not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-cart-details/{userId}")
    public ResponseEntity<List<CartResponse>> getCartDetails(@PathVariable Long userId) {
        logger.info("Fetching cart details for user ID: {}", userId);
        List<CartResponse> cartItems = cartService.getCartDetails(userId);
        logger.debug("Found {} cart items for user ID: {}", cartItems.size(), userId);
        return ResponseEntity.ok(cartItems);
    }

    @PutMapping("/update-cart-quantity/{cartId}")
    public ResponseEntity<?> updateCartQuantity(@PathVariable Long cartId, @RequestBody Map<String, Integer> request) {
        logger.info("Request received to update quantity for cart ID: {} with data: {}", cartId, request);
        Map<String, Object> response = new HashMap<>();
        try {
            Integer quantity = request.get("quantity");
            if (quantity == null || quantity < 1) {
                logger.warn("Invalid quantity provided for cart ID {}: quantity is null or less than 1.", cartId);
                response.put("error", "Quantity must be 1 or more.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            Cart updatedCart = cartService.updateQuantity(cartId, quantity);
            CartResponse responseDto = new CartResponse(updatedCart);

            response.put("message", "Quantity and Total Price updated successfully");
            response.put("data", responseDto);
            logger.info("Successfully updated quantity for cart ID {} to {}.", cartId, quantity);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            logger.warn("ResourceNotFoundException while updating cart ID {}: {}", cartId, e.getMessage());
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Unexpected error while updating quantity for cart ID {}.", cartId, e);
            response.put("error", "Failed to update quantity: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
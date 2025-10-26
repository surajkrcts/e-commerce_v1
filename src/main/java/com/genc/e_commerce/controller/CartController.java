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

// @RestController combines @Controller and @ResponseBody, meaning methods in this class will return JSON responses.
@RestController
// @RequestMapping sets the base URL for all endpoints in this controller to "/api".
@RequestMapping("/api")
// @CrossOrigin allows requests from any origin, useful for frontend applications running on a different domain.
@CrossOrigin(origins = "*")
public class CartController {
    // Initializes a logger for this class to log controller-level events.
    private static final Logger logger = LogManager.getLogger(CartController.class);

    // @Autowired injects the CartService to handle business logic.
    @Autowired
    private CartService cartService;

    /**
     * Endpoint to add a product to the user's cart.
     * @param cartRequest The request body containing userId, productId, and quantity.
     * @return A ResponseEntity with the result of the operation.
     */
    @PostMapping("/add-product-to-cart")
    public ResponseEntity<?> addToCart(@Valid @RequestBody CartRequest cartRequest) {
        logger.debug("Request received to add product to cart: {}", cartRequest);
        // A map is used to build a custom JSON response.
        Map<String, Object> response = new HashMap<>();
        try {
            // Call the service layer to perform the add-to-cart logic.
            Cart cart = cartService.addToCart(cartRequest);
            // Convert the saved Cart entity to a response DTO.
            CartResponse cartResponse = new CartResponse(cart);
            response.put("message", "product added in the cart");
            response.put("data", cartResponse);

            logger.info("Product {} added to cart for user {}.", cartRequest.getProductId(), cartRequest.getUserId());
            // Return a 201 CREATED status on success.
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            // Catch any unexpected errors during the process.
            logger.error("Error occurred while adding product to cart for request: {}", cartRequest, e);
            response.put("message", "an error occured while adding product in cart");
            // Return a 500 INTERNAL_SERVER_ERROR status on failure.
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to delete an item from the cart by its unique ID.
     * @param cartId The ID of the cart item to be deleted, passed in the URL path.
     * @return A ResponseEntity indicating success or failure.
     */
    @DeleteMapping("/delete-cart-byId/{cartId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long cartId) {
        logger.info("Request received to remove cart item with ID: {}", cartId);
        Map<String, Object> response = new HashMap<>();

        // Call the service to delete the item. The service returns the deleted item if it was found.
        Optional<Cart> deleteCart = cartService.removeFromCart(cartId);

        // Check if the service found and deleted the item.
        if (deleteCart.isPresent()) {
            Cart cart = deleteCart.get();
            CartResponse cartResponse = new CartResponse(cart);
            response.put("message", "cart item deleted successfully");
            response.put("data", cartResponse);
            logger.info("Successfully deleted cart item with ID: {}", cartId);
            // Return a 200 OK status with the deleted item's data.
            return ResponseEntity.ok(response);
        } else {
            // If the item was not found.
            logger.warn("Attempted to delete a non-existent cart item with ID: {}", cartId);
            response.put("error", "cart item with cart id " + cartId + " not found");
            // Return a 404 NOT_FOUND status.
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint to retrieve all items in a specific user's cart.
     * @param userId The ID of the user whose cart is being requested.
     * @return A ResponseEntity containing a list of cart items.
     */
    @GetMapping("/get-cart-details/{userId}")
    public ResponseEntity<List<CartResponse>> getCartDetails(@PathVariable Long userId) {
        logger.info("Fetching cart details for user ID: {}", userId);
        // Call the service to get all cart items for the user.
        List<CartResponse> cartItems = cartService.getCartDetails(userId);
        logger.debug("Found {} cart items for user ID: {}", cartItems.size(), userId);
        // Return a 200 OK status with the list of cart items.
        return ResponseEntity.ok(cartItems);
    }

    /**
     * Endpoint to update the quantity of a specific item in the cart.
     * @param cartId The ID of the cart item to update.
     * @param request A JSON object in the request body, e.g., {"quantity": 3}.
     * @return A ResponseEntity with the result of the update operation.
     */
    @PutMapping("/update-cart-quantity/{cartId}")
    public ResponseEntity<?> updateCartQuantity(@PathVariable Long cartId, @RequestBody Map<String, Integer> request) {
        logger.info("Request received to update quantity for cart ID: {} with data: {}", cartId, request);
        Map<String, Object> response = new HashMap<>();
        try {
            // Extract the quantity from the request body map.
            Integer quantity = request.get("quantity");
            // Validate the quantity.
            if (quantity == null || quantity < 1) {
                logger.warn("Invalid quantity provided for cart ID {}: quantity is null or less than 1.", cartId);
                response.put("error", "Quantity must be 1 or more.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Call the service to update the quantity.
            Cart updatedCart = cartService.updateQuantity(cartId, quantity);
            CartResponse responseDto = new CartResponse(updatedCart);

            response.put("message", "Quantity and Total Price updated successfully");
            response.put("data", responseDto);
            logger.info("Successfully updated quantity for cart ID {} to {}.", cartId, quantity);
            // Return a 200 OK status on success.
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            // Handle the case where the cart item to update is not found.
            logger.warn("ResourceNotFoundException while updating cart ID {}: {}", cartId, e.getMessage());
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Handle any other unexpected errors.
            logger.error("Unexpected error while updating quantity for cart ID {}.", cartId, e);
            response.put("error", "Failed to update quantity: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
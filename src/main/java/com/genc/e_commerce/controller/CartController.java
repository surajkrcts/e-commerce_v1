package com.genc.e_commerce.controller;

import com.genc.e_commerce.dto.CartRequest;
import com.genc.e_commerce.dto.CartResponse;
import com.genc.e_commerce.entity.Cart;
import com.genc.e_commerce.exception.ResourceNotFoundException;
import com.genc.e_commerce.service.CartService;
import jakarta.validation.Valid;
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
    @Autowired
    CartService cartService;

    @PostMapping("/add-product-to-cart")
    public ResponseEntity<?> addToCart(@Valid @RequestBody CartRequest cartRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            Cart cart = cartService.addToCart(cartRequest);
            CartResponse cartResponse = new CartResponse(cart);
            response.put("message", "product added in the cart");
            response.put("data", cartResponse);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("message", "an error occured while adding product in cart");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/delete-cart-byId/{cartId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long cartId) {

        Optional<Cart> deleteCart = cartService.removeFromCart(cartId);
        Map<String, Object> response = new HashMap<>();

        if(deleteCart.isPresent()){
            Cart cart=deleteCart.get();
            CartResponse cartResponse=new CartResponse(cart);
            response.put("message","cart item deleted successfully");
            response.put("data",cartResponse);
            return ResponseEntity.ok(response);
        }else{
            response.put("error","cart item with cart id "+cartId+" not found");
            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        }


    }

@GetMapping("/get-cart-details/{userId}")
public ResponseEntity<List<CartResponse>> getCartDetails(@PathVariable Long userId){
      List<CartResponse> cartItem=cartService.getCartDetails(userId);
        return ResponseEntity.ok(cartItem);

}

    @PutMapping("/update-cart-quantity/{cartId}")
    public ResponseEntity<?> updateCartQuantity(@PathVariable Long cartId, @RequestBody Map<String, Integer> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Integer quantity = request.get("quantity");
            if (quantity == null || quantity < 1) {
                response.put("error", "Quantity must be 1 or more.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            Cart updatedCart = cartService.updateQuantity(cartId, quantity);
            CartResponse responseDto = new CartResponse(updatedCart);

            response.put("message", "Quantity and Total Price updated successfully");
            response.put("data", responseDto);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("error", "Failed to update quantity: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}

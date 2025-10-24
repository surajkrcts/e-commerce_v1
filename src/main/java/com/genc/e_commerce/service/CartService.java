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

@Slf4j
@Service
public class CartService {
    @Autowired
    private CartRepo cartRepo;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepo productRepo;


    @Transactional
    public Cart addToCart(CartRequest cartRequest) {
        log.info("Attempting to add product to cart for user ID:{} and product ID:{}",
                cartRequest.getUserId(), cartRequest.getProductId());

        User user = userRepository.findById(cartRequest.getUserId()).
                orElseThrow(() -> new ResourceNotFoundException("User not found"));
        log.debug("User found: {}", user.getUsername());

        Product product = productRepo.findById(cartRequest.getProductId()).
                orElseThrow(() -> new ResourceNotFoundException("Prodcut not found"));
        log.debug("Product found: {}", product.getName());

        Optional<Cart> existingCartItem = cartRepo.findByUserAndProduct(user, product);
        double unitprice=product.getPrice();
        if (existingCartItem.isPresent()) {
            Cart cart = existingCartItem.get();
            int updateQuantity = cart.getQuantity() + cartRequest.getQuantity();
            cart.setQuantity(updateQuantity);
            cart.setItemPriceTotal(unitprice*updateQuantity);
            log.info("Existing cart item updated. New Quantity: {}", updateQuantity);
            return cartRepo.save(cart);
        } else {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setProduct(product);
            cart.setQuantity(cartRequest.getQuantity());
            cart.setItemPriceTotal(unitprice*cartRequest.getQuantity());
            log.info("New cart item created with Quantity: {}", cartRequest.getQuantity());
            return cartRepo.save(cart);
        }
    }


    @Transactional
    public Optional<Cart> removeFromCart(Long cartId) {
        log.info("Attempting to find and remove cart item using cart ID:{}", cartId);
        Optional<Cart> existingCart = cartRepo.findById(cartId);
        if (existingCart.isPresent()) {
            cartRepo.deleteById(cartId);
            log.info("Cart item successfully deleted: {}", cartId);
            return existingCart;
        }
        log.warn("Cart item not found for removal: {}", cartId);
        return Optional.empty();
    }

    public List<CartResponse> getCartDetails(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("No user found"));

        List<Cart> cartItem = cartRepo.findByUserUserId(userId);


        return cartItem.stream()
                .map(CartResponse::new) // Uses the CartResponse(Cart cart) constructor
                .collect(Collectors.toList());
    }

    @Transactional
    public Cart updateQuantity(Long cartId, int newQuantity){

       if (newQuantity<1){
           throw new IllegalArgumentException("quantity must not be less than 1");
       }
       Cart cart=cartRepo.findById(cartId)
               .orElseThrow(()-> new RuntimeException("No cart item found with the cart ID "+cartId));

       double unitprice=cart.getProduct().getPrice();
       cart.setQuantity(newQuantity);

       double newItemPriceTotal=unitprice*newQuantity;
       cart.setItemPriceTotal(newItemPriceTotal);

        log.info("Cart item ID {} quantity updated to {}. New Total: ${}", cartId, newQuantity, newItemPriceTotal);

        return cartRepo.save(cart);
    }
}



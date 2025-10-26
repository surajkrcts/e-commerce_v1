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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    // 1. Create mocks for the repository dependencies
    @Mock
    private CartRepo cartRepo;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepo productRepo;

    // 2. Inject the mocks into the service instance
    @InjectMocks
    private CartService cartService;

    private User testUser;
    private Product testProduct;
    private CartRequest cartRequest;

    // 3. Set up common test data before each test
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);

        testProduct = new Product();
        testProduct.setProductId(10L);
        testProduct.setPrice(100.0); // Unit price

        cartRequest = new CartRequest();
        cartRequest.setUserId(1L);
        cartRequest.setProductId(10L);
        cartRequest.setQuantity(2);
    }

    // ## Tests for addToCart ##
    //----------------------------------------------------------------------

    @Test
    void addToCart_whenItemIsNew_shouldCreateNewCartItem() {
        // --- ARRANGE ---
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepo.findById(10L)).thenReturn(Optional.of(testProduct));
        when(cartRepo.findByUserAndProduct(testUser, testProduct)).thenReturn(Optional.empty()); // Item is new
        when(cartRepo.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- ACT ---
        Cart result = cartService.addToCart(cartRequest);

        // --- ASSERT ---
        assertNotNull(result);
        assertEquals(2, result.getQuantity());
        assertEquals(200.0, result.getItemPriceTotal()); // 100.0 * 2
        verify(cartRepo, times(1)).save(any(Cart.class));
    }

    @Test
    void addToCart_whenItemExists_shouldUpdateQuantityAndPrice() {
        // --- ARRANGE ---
        Cart existingCartItem = new Cart();
        existingCartItem.setQuantity(3); // Already 3 in cart
        existingCartItem.setUser(testUser);
        existingCartItem.setProduct(testProduct);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepo.findById(10L)).thenReturn(Optional.of(testProduct));
        when(cartRepo.findByUserAndProduct(testUser, testProduct)).thenReturn(Optional.of(existingCartItem));
        when(cartRepo.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- ACT ---
        Cart result = cartService.addToCart(cartRequest); // Add 2 more

        // --- ASSERT ---
        assertNotNull(result);
        assertEquals(5, result.getQuantity()); // 3 + 2 = 5
        assertEquals(500.0, result.getItemPriceTotal()); // 100.0 * 5
        verify(cartRepo, times(1)).save(existingCartItem);
    }

    @Test
    void addToCart_whenUserNotFound_shouldThrowException() {
        // --- ARRANGE ---
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // --- ACT & ASSERT ---
        assertThrows(ResourceNotFoundException.class, () -> cartService.addToCart(cartRequest));
        verify(productRepo, never()).findById(anyLong()); // Ensure we don't proceed
        verify(cartRepo, never()).save(any(Cart.class));
    }

    // ## Tests for removeFromCart ##
    //----------------------------------------------------------------------

    @Test
    void removeFromCart_whenCartItemExists_shouldDeleteAndReturnItem() {
        // --- ARRANGE ---
        Cart cartItem = new Cart();
        cartItem.setCartId(50L);
        when(cartRepo.findById(50L)).thenReturn(Optional.of(cartItem));
        doNothing().when(cartRepo).deleteById(50L);

        // --- ACT ---
        Optional<Cart> result = cartService.removeFromCart(50L);

        // --- ASSERT ---
        assertTrue(result.isPresent());
        assertEquals(50L, result.get().getCartId());
        verify(cartRepo, times(1)).deleteById(50L);
    }

    @Test
    void removeFromCart_whenCartItemDoesNotExist_shouldReturnEmpty() {
        // --- ARRANGE ---
        when(cartRepo.findById(99L)).thenReturn(Optional.empty());

        // --- ACT ---
        Optional<Cart> result = cartService.removeFromCart(99L);

        // --- ASSERT ---
        assertFalse(result.isPresent());
        verify(cartRepo, never()).deleteById(anyLong());
    }

    // ## Tests for getCartDetails ##
    //----------------------------------------------------------------------

    @Test
    void getCartDetails_whenUserExists_shouldReturnCartResponseList() {
        // --- ARRANGE ---
        // This is the correct way
        Cart cartItem1 = new Cart();
        cartItem1.setCartId(1L);
        cartItem1.setUser(testUser);
        cartItem1.setProduct(testProduct);
        cartItem1.setQuantity(2);
        cartItem1.setItemPriceTotal(200.0);
        List<Cart> cartList = Collections.singletonList(cartItem1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepo.findByUserUserId(1L)).thenReturn(cartList);

        // --- ACT ---
        List<CartResponse> result = cartService.getCartDetails(1L);

        // --- ASSERT ---
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getProductId());
        assertEquals(2, result.get(0).getQuantity());
    }

    // ## Tests for updateQuantity ##
    //----------------------------------------------------------------------

    @Test
    void updateQuantity_whenCartItemExists_shouldUpdateAndSave() {
        // --- ARRANGE ---
        // This is the correct way
        Cart cartItem1 = new Cart();
        cartItem1.setCartId(1L);
        cartItem1.setUser(testUser);
        cartItem1.setProduct(testProduct);
        cartItem1.setQuantity(2);
        cartItem1.setItemPriceTotal(200.0);
        when(cartRepo.findById(1L)).thenReturn(Optional.of(cartItem1));
        when(cartRepo.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- ACT ---
        Cart result = cartService.updateQuantity(1L, 5); // New quantity is 5

        // --- ASSERT ---
        assertNotNull(result);
        assertEquals(5, result.getQuantity());
        assertEquals(500.0, result.getItemPriceTotal()); // 100.0 * 5
        verify(cartRepo, times(1)).save(cartItem1);
    }

    @Test
    void updateQuantity_whenQuantityIsLessThanOne_shouldThrowException() {
        // --- ACT & ASSERT ---
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartService.updateQuantity(1L, 0); // Invalid quantity
        });

        assertEquals("quantity must not be less than 1", exception.getMessage());
        verify(cartRepo, never()).findById(anyLong());
        verify(cartRepo, never()).save(any(Cart.class));
    }
}
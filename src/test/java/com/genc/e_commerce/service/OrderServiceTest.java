package com.genc.e_commerce.service;

import com.genc.e_commerce.dto.OrderRequest;
import com.genc.e_commerce.entity.Cart;
import com.genc.e_commerce.entity.Order;
import com.genc.e_commerce.entity.User;
import com.genc.e_commerce.exception.ResourceNotFoundException;
import com.genc.e_commerce.repository.CartRepo;
import com.genc.e_commerce.repository.OrderRepo;
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

// Enable Mockito for JUnit 5
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    // 1. Create mocks for all repository dependencies
    @Mock
    private OrderRepo orderRepo;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartRepo cartRepo;

    // 2. Inject the mocks into an instance of OrderService
    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private OrderRequest orderRequest;

    // 3. Set up common objects to be used in the tests
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("Test User");

        orderRequest = new OrderRequest();
        orderRequest.setUserId(1L);
        orderRequest.setTotalAmount(500.00);
    }

    @Test
    void createOrder_whenUserExistsAndCartIsNotEmpty_shouldCreateNewOrder() {
        // --- ARRANGE ---
        List<Cart> cartItems = Collections.singletonList(new Cart()); // Simulate a non-empty cart

        // Mock repository calls
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(orderRepo.findByUserUserIdAndStatus(1L, Order.Status.PENDING)).thenReturn(Optional.empty()); // No existing pending order
        when(cartRepo.findByUserUserId(1L)).thenReturn(cartItems);
        when(orderRepo.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return the saved order

        // --- ACT ---
        Order createdOrder = orderService.createOrder(orderRequest);

        // --- ASSERT ---
        assertNotNull(createdOrder);
        assertEquals(Order.Status.PENDING, createdOrder.getStatus());
        assertEquals(500.00, createdOrder.getTotalAmount());
        assertEquals(testUser, createdOrder.getUser());

        // Verify that the necessary repository methods were called
        verify(orderRepo, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_whenUserNotFound_shouldThrowRuntimeException() {
        // --- ARRANGE ---
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // --- ACT & ASSERT ---
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(orderRequest);
        });

        assertEquals("Cannot create order: User not found with ID 1", exception.getMessage());
    }

    @Test
    void createOrder_whenCartIsEmpty_shouldThrowResourceNotFoundException() {
        // --- ARRANGE ---
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(orderRepo.findByUserUserIdAndStatus(1L, Order.Status.PENDING)).thenReturn(Optional.empty());
        when(cartRepo.findByUserUserId(1L)).thenReturn(Collections.emptyList()); // Simulate an empty cart

        // --- ACT & ASSERT ---
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            orderService.createOrder(orderRequest);
        });

        assertEquals("Cannot create order: Your cart is currently empty.", exception.getMessage());
    }

    @Test
    void createOrder_whenPendingOrderAlreadyExists_shouldReturnExistingOrder() {
        // --- ARRANGE ---
        Order existingPendingOrder = new Order();
        existingPendingOrder.setOrderId(99L);
        existingPendingOrder.setStatus(Order.Status.PENDING);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(orderRepo.findByUserUserIdAndStatus(1L, Order.Status.PENDING)).thenReturn(Optional.of(existingPendingOrder));

        // --- ACT ---
        Order resultOrder = orderService.createOrder(orderRequest);

        // --- ASSERT ---
        assertNotNull(resultOrder);
        assertEquals(99L, resultOrder.getOrderId()); // Should be the existing order

        // Verify that these methods were NEVER called because the logic should exit early
        verify(cartRepo, never()).findByUserUserId(anyLong());
        verify(orderRepo, never()).save(any(Order.class));
    }

    @Test
    void getOrderDetails_whenOrderExists_shouldReturnOrder() {
        // --- ARRANGE ---
        Order order = new Order();
        order.setOrderId(50L);
        when(orderRepo.findById(50L)).thenReturn(Optional.of(order));

        // --- ACT ---
        Order foundOrder = orderService.getOrderDetails(50L);

        // --- ASSERT ---
        assertNotNull(foundOrder);
        assertEquals(50L, foundOrder.getOrderId());
    }

    @Test
    void getOrderDetails_whenOrderNotFound_shouldThrowRuntimeException() {
        // --- ARRANGE ---
        when(orderRepo.findById(999L)).thenReturn(Optional.empty());

        // --- ACT & ASSERT ---
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderDetails(999L);
        });

        assertEquals("Order not found with ID: 999", exception.getMessage());
    }
}
package com.genc.e_commerce.service;

import com.genc.e_commerce.entity.*;
import com.genc.e_commerce.repository.CartRepo;
import com.genc.e_commerce.repository.OrderRepo;
import com.genc.e_commerce.repository.PaymentRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Use the Mockito extension for JUnit 5
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    // 1. Create mocks for all dependencies of PaymentService
    @Mock
    private PaymentRepo paymentRepo;

    @Mock
    private OrderRepo orderRepo;

    @Mock
    private CartRepo cartRepo;

    // 2. Create an instance of the service and inject the mocks into it
    @InjectMocks
    private PaymentService paymentService;

    private Order testOrder;
    private User testUser;

    // A setup method to run before each test
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);

        testOrder = new Order();
        testOrder.setOrderId(100L);
        testOrder.setUser(testUser);
        testOrder.setTotalAmount(1500.0);
        testOrder.setStatus(Order.Status.SHIPPED);
    }


    @Test
    void processPayment_WhenPaymentIsSuccessful_ShouldUpdateStatusAndClearCart() {
        // --- ARRANGE ---
        // Define the behavior of our mocks for this specific test case
        when(orderRepo.findById(100L)).thenReturn(Optional.of(testOrder));
        when(cartRepo.findByUserUserId(1L)).thenReturn(Collections.singletonList(new Cart())); // Simulate an existing cart

        // --- ACT ---
        // Call the method we want to test
        String result = paymentService.processPayment(100L, true, Payment.PaymentMethod.CARD);

        // --- ASSERT ---
        // Verify the results
        assertEquals("Payment Processed Successfully", result);
        assertEquals(Order.Status.SHIPPED, testOrder.getStatus()); // Check if the order status was updated correctly

        // Verify that the save methods were called on the repositories
        verify(orderRepo, times(1)).save(testOrder);
        verify(paymentRepo, times(1)).save(any(Payment.class));

        // Verify that the cart was deleted
        verify(cartRepo, times(1)).deleteAll(anyList());
    }

    @Test
    void processPayment_WhenPaymentFails_ShouldUpdateStatusToCancelled() {
        // --- ARRANGE ---
        when(orderRepo.findById(100L)).thenReturn(Optional.of(testOrder));

        // --- ACT ---
        String result = paymentService.processPayment(100L, false, Payment.PaymentMethod.CARD);

        // --- ASSERT ---
        assertEquals("Payment Processed Successfully", result);
        assertEquals(Order.Status.CANCELLED, testOrder.getStatus());

        verify(orderRepo, times(1)).save(testOrder);
        verify(paymentRepo, times(1)).save(any(Payment.class));

        // IMPORTANT: Verify that the cart deletion method was *never* called
        verify(cartRepo, never()).deleteAll(anyList());
    }

    @Test
    void processPayment_WhenOrderNotFound_ShouldThrowException() {
        // --- ARRANGE ---
        // Mock the repository to find nothing
        when(orderRepo.findById(999L)).thenReturn(Optional.empty());

        // --- ACT & ASSERT ---
        // Assert that calling the method with a non-existent ID throws the expected exception
        assertThrows(RuntimeException.class, () -> {
            paymentService.processPayment(999L, true, Payment.PaymentMethod.CARD);
        });
    }

    @Test
    void getPaymentStatus_WhenPaymentExists_ShouldReturnStatus() {
        // --- ARRANGE ---
        Payment mockPayment = new Payment();
        mockPayment.setPaymentId(1L);
        mockPayment.setPaymentStatus(Payment.PaymentStatus.COMPLETED);
        when(paymentRepo.findById(1L)).thenReturn(Optional.of(mockPayment));

        // --- ACT ---
        Payment.PaymentStatus status = paymentService.getPaymentStatus(1L);

        // --- ASSERT ---
        assertEquals(Payment.PaymentStatus.COMPLETED, status);
    }
}
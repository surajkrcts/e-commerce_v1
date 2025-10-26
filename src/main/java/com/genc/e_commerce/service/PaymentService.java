package com.genc.e_commerce.service;

import com.genc.e_commerce.entity.Cart;
import com.genc.e_commerce.entity.Order;
import com.genc.e_commerce.entity.Payment;
import com.genc.e_commerce.exception.ResourceNotFoundException;
import com.genc.e_commerce.repository.CartRepo;
import com.genc.e_commerce.repository.OrderRepo;
import com.genc.e_commerce.repository.PaymentRepo;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

// @Service annotation marks this class as a Spring service component to handle business logic.
@Service
public class PaymentService {

    // Initializes a logger for this class to log information, warnings, and errors.
    private static final Logger logger = LogManager.getLogger(PaymentService.class);

    // Repositories for database interaction, injected via the constructor.
    private final PaymentRepo paymentRepo;
    private final OrderRepo orderRepo;
    private final CartRepo cartRepo;

    /**
     * Constructor for PaymentService.
     * @Autowired tells Spring to inject the required repository beans.
     */
    @Autowired
    public PaymentService(PaymentRepo paymentRepo, OrderRepo orderRepo, CartRepo cartRepo) {
        this.paymentRepo = paymentRepo;
        this.orderRepo = orderRepo;
        this.cartRepo = cartRepo;
    }

    /**
     * Processes a payment for a given order.
     * This method is transactional, meaning all database operations within it will either
     * succeed together or fail together, ensuring data consistency.
     * @param orderId The ID of the order to process the payment for.
     * @param isPaymentSuccess A boolean indicating if the payment gateway reported success.
     * @param paymentMethod The method of payment (e.g., CREDIT_CARD, UPI).
     * @return A success message string.
     */
    @Transactional
    public String processPayment(Long orderId, boolean isPaymentSuccess, Payment.PaymentMethod paymentMethod) {

        logger.info("Attempting to process payment for orderId: {}", orderId);

        try {
            // Find the order by its ID, or throw an exception if it doesn't exist.
            Order order = orderRepo.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

            // Get the user ID from the order to use for deleting the cart later.
            Long userId = order.getUser().getUserId();

            // Create a new Payment entity to record this transaction.
            Payment payment = new Payment();
            payment.setAmount(order.getTotalAmount());
            payment.setPaymentMethod(paymentMethod);
            payment.setPaymentDate(new Date());

            logger.debug("Payment method for orderId {}: {}", orderId, paymentMethod);

            // Logic to handle successful or failed payments.
            if (isPaymentSuccess) {
                payment.setPaymentStatus(Payment.PaymentStatus.COMPLETED);
                order.setStatus(Order.Status.SHIPPED);
                logger.info("Payment successful for orderId: {}. Order status set to SHIPPED.", orderId);
                // If payment is successful, clear the user's cart.
                deleteCartAfterPayment(userId);
            } else {
                payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
                order.setStatus(Order.Status.CANCELLED);
                logger.warn("Payment failed for orderId: {}. Order status set to CANCELLED.", orderId);
            }

            // Save the updated order status to the database.
            orderRepo.save(order);
            // Link the payment to the order and save the payment record.
            payment.setOrder(order);
            paymentRepo.save(payment);

            logger.info("Payment processed and saved successfully for orderId: {}", orderId);
            return "Payment Processed Successfully";

        } catch (Exception e) {
            // If any error occurs, re-throw it as a RuntimeException to trigger a transaction rollback.
            throw new RuntimeException("Payment processing failed: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves the status of a specific payment.
     * @param paymentId The ID of the payment to check.
     * @return The PaymentStatus enum (e.g., COMPLETED, FAILED).
     */
    public Payment.PaymentStatus getPaymentStatus(Long paymentId) {
        logger.debug("Fetching payment status for paymentId: {}", paymentId);

        // Find the payment by its ID, or throw an exception if not found.
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> {
                    logger.error("Payment not found for paymentId: {}", paymentId);
                    return new RuntimeException("Payment not found");
                });

        logger.debug("Status for paymentId {}: {}", paymentId, payment.getPaymentStatus());
        return payment.getPaymentStatus();
    }

    /**
     * Deletes all items from a user's cart, typically after a successful payment.
     * @param userId The ID of the user whose cart should be cleared.
     * @return A success message string.
     */
    public String deleteCartAfterPayment(Long userId) {
        // Find all cart items associated with the user.
        List<Cart> cartList = cartRepo.findByUserUserId(userId);
        // If the cart is already empty, throw an exception.
        if (cartList.isEmpty()) {
            throw new ResourceNotFoundException("no cart item found");
        }
        // Delete all found cart items from the database.
        cartRepo.deleteAll(cartList);
        return "existing user id cart deleted successfully";
    }
}
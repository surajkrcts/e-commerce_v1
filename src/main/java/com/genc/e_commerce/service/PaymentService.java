package com.genc.e_commerce.service;

import com.genc.e_commerce.entity.Order;
import com.genc.e_commerce.entity.Payment;
import com.genc.e_commerce.repository.OrderRepo;
import com.genc.e_commerce.repository.PaymentRepo;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
@Service
public class PaymentService {

    private static final Logger logger = LogManager.getLogger(PaymentService.class);

    private final PaymentRepo paymentRepo;
    private final OrderRepo orderRepo;




    @Autowired
    public PaymentService(PaymentRepo paymentRepo, OrderRepo orderRepo) {
        this.paymentRepo = paymentRepo;
        this.orderRepo = orderRepo;
    }

    @Transactional
    public String processPayment(Long orderId, boolean isPaymentSuccess, Payment.PaymentMethod paymentMethod) {

        logger.info("Attempting to process payment for orderId: {}", orderId);

        try {
            Order order = orderRepo.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

            Payment payment = new Payment();
            payment.setAmount(order.getTotalAmount());
            payment.setPaymentMethod(paymentMethod);
            payment.setPaymentDate(new Date());

            logger.debug("Payment method for orderId {}: {}", orderId, paymentMethod);

            if (isPaymentSuccess) {
                payment.setPaymentStatus(Payment.PaymentStatus.COMPLETED);
                order.setStatus(Order.Status.SHIPPED);
                logger.info("Payment successful for orderId: {}. Order status set to PENDING.", orderId);
            } else {
                payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
                order.setStatus(Order.Status.CANCELLED);
                logger.warn("Payment failed for orderId: {}. Order status set to CANCELLED.", orderId);
            }

            orderRepo.save(order);
            payment.setOrder(order);
            paymentRepo.save(payment);

            logger.info("Payment processed and saved successfully for orderId: {}", orderId);
            return "Payment Processed Successfully";

        } catch (Exception e) {
//            logger.error("Error processing payment for orderId: {}", orderId, e);
            throw new RuntimeException("Payment processing failed: " + e.getMessage(), e);
        }
    }

    public Payment.PaymentStatus getPaymentStatus(Long paymentId) {
        logger.debug("Fetching payment status for paymentId: {}", paymentId);

        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> {
                    logger.error("Payment not found for paymentId: {}", paymentId);
                    return new RuntimeException("Payment not found");
                });

        logger.debug("Status for paymentId {}: {}", paymentId, payment.getPaymentStatus());
        return payment.getPaymentStatus();
    }
}

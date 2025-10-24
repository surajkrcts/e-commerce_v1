package com.genc.e_commerce.controller;

import com.genc.e_commerce.entity.Payment;
import com.genc.e_commerce.service.PaymentService;
import com.genc.e_commerce.util.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    private static final Logger logger = LogManager.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(value = "/process")
    public Response processPayment(@RequestParam Long orderId, @RequestParam boolean isPaymentSuccess, @RequestParam Payment.PaymentMethod paymentMethod) {
        logger.info("Received payment processing request for orderId: {}", orderId);
        logger.debug("Payment details: isPaymentSuccess={}, paymentMethod={}", isPaymentSuccess, paymentMethod);

        try {
            String result = paymentService.processPayment(orderId, isPaymentSuccess, paymentMethod);
            logger.info("Successfully processed payment for orderId: {}", orderId);
            return new Response(result);
        } catch (Exception e) {
            logger.error("Error processing payment for orderId: {}", orderId, e);
            return new Response("Error: " + e.getMessage());
        }
    }

    @GetMapping(value = "/get-payment-status")
    public Response getPaymentStatus(@RequestParam Long paymentId)
    {
        logger.info("Received request to get payment status for paymentId: {}", paymentId);

        try {
            Payment.PaymentStatus status = paymentService.getPaymentStatus(paymentId);
            logger.debug("Returning status {} for paymentId: {}", status, paymentId);
            return new Response(status);
        } catch (Exception e) {
            logger.error("Error retrieving status for paymentId: {}", paymentId, e);
            return new Response("Error: " + e.getMessage());
        }
    }
}

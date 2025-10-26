package com.genc.e_commerce.controller;

import com.genc.e_commerce.entity.Payment;
import com.genc.e_commerce.service.PaymentService;
import com.genc.e_commerce.util.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

// @RestController marks this class as a Spring REST controller, where each method returns a domain object instead of a view.
@RestController
// @RequestMapping sets the base URL for all endpoints in this controller to "/payment".
@RequestMapping(value = "/payment")
// @CrossOrigin allows requests from any origin, which is useful for integrating with a frontend application.
@CrossOrigin(origins = "*")
public class PaymentController {

    // Initializes a logger for this class to log events.
    private static final Logger logger = LogManager.getLogger(PaymentController.class);

    // The service layer dependency that handles the business logic for payments.
    private final PaymentService paymentService;

    /**
     * Constructor for PaymentController.
     * Spring uses this for dependency injection, providing an instance of PaymentService.
     * @param paymentService The payment service to be injected.
     */
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Endpoint to process a payment for an order.
     * It takes orderId, payment success status, and payment method as request parameters.
     * @param orderId The ID of the order being paid for.
     * @param isPaymentSuccess A boolean indicating if the payment was successful.
     * @param paymentMethod The method used for the payment (e.g., CREDIT_CARD).
     * @return A Response object containing a success or error message.
     */
    @PostMapping(value = "/process")
    public Response processPayment(@RequestParam Long orderId, @RequestParam boolean isPaymentSuccess, @RequestParam Payment.PaymentMethod paymentMethod) {
        logger.info("Received payment processing request for orderId: {}", orderId);
        logger.debug("Payment details: isPaymentSuccess={}, paymentMethod={}", isPaymentSuccess, paymentMethod);

        try {
            // Call the service layer to handle the payment logic.
            String result = paymentService.processPayment(orderId, isPaymentSuccess, paymentMethod);
            logger.info("Successfully processed payment for orderId: {}", orderId);
            // Return a successful response.
            return new Response(result);
        } catch (Exception e) {
            // Catch any exceptions that occur during payment processing.
            logger.error("Error processing payment for orderId: {}", orderId, e);
            // Return a response containing the error message.
            return new Response("Error: " + e.getMessage());
        }
    }

    /**
     * Endpoint to retrieve the status of a specific payment.
     * @param paymentId The unique ID of the payment to check.
     * @return A Response object containing the payment status or an error message.
     */
    @GetMapping(value = "/get-payment-status")
    public Response getPaymentStatus(@RequestParam Long paymentId)
    {
        logger.info("Received request to get payment status for paymentId: {}", paymentId);

        try {
            // Call the service layer to get the payment status.
            Payment.PaymentStatus status = paymentService.getPaymentStatus(paymentId);
            logger.debug("Returning status {} for paymentId: {}", status, paymentId);
            // Return a successful response with the payment status.
            return new Response(status);
        } catch (Exception e) {
            // Catch any exceptions, such as the payment not being found.
            logger.error("Error retrieving status for paymentId: {}", paymentId, e);
            // Return a response containing the error message.
            return new Response("Error: " + e.getMessage());
        }
    }
}
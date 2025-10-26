package com.genc.e_commerce.controller;

import com.genc.e_commerce.dto.OrderRequest;
import com.genc.e_commerce.dto.OrderResponse;
import com.genc.e_commerce.entity.Order;
import com.genc.e_commerce.service.OrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

// @RestController marks this class as a Spring REST controller, handling incoming web requests.
@RestController
// @CrossOrigin allows requests from any origin, which is useful for frontend applications.
@CrossOrigin(origins = "*")
public class OrderController {
    // Initializes a logger for this class to log information and errors.
    private static final Logger logger = LogManager.getLogger(OrderController.class);

    // @Autowired injects an instance of OrderService to handle the business logic.
    @Autowired
    private OrderService orderService;

    /**
     * Endpoint to create a new order.
     * @param orderRequest The request body containing the necessary information to create an order.
     * @return A ResponseEntity with the created order details or an error message.
     */
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        logger.info("Request received to create an order for user ID: {}", orderRequest.getUserId());
        logger.debug("Order creation request payload: {}", orderRequest);

        try {
            // Call the service layer to perform the order creation logic.
            Order orderDetail = orderService.createOrder(orderRequest);
            // Map the resulting Order entity to a response DTO.
            OrderResponse orderResponse = new OrderResponse(orderDetail);

            logger.info("Successfully created order with ID: {} for user ID: {}", orderDetail.getOrderId(), orderDetail.getUser().getUserId());
            // Return a 201 CREATED status with the order response.
            return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);

        } catch (RuntimeException e) {
            // Catch specific runtime exceptions (like user not found, empty cart) for a client error response.
            logger.warn("Bad request while creating order for user ID {}: {}", orderRequest.getUserId(), e.getMessage());
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());
            // Return a 400 BAD_REQUEST status.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);

        } catch (Exception e) {
            // Catch any other unexpected exceptions for a server error response.
            logger.error("Internal server error while creating order for user ID: {}", orderRequest.getUserId(), e);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("error", "Order creation failed due to a server error.");
            // Return a 500 INTERNAL_SERVER_ERROR status.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }

    /**
     * Endpoint to fetch the details of a specific order by its ID.
     * @param orderId The ID of the order to retrieve, passed in the URL path.
     * @return A ResponseEntity containing the order details or an error message.
     */
    @GetMapping("/order-details/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable Long orderId) {
        logger.info("Request received to fetch details for order ID: {}", orderId);
        try {
            // Call the service layer to get the order details.
            Order orderDetail = orderService.getOrderDetails(orderId);
            // Map the Order entity to a response DTO.
            OrderResponse response = new OrderResponse(orderDetail);
            logger.info("Successfully fetched details for order ID: {}", orderId);
            // Return a 200 OK status with the order details.
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Catch the exception thrown if the order is not found.
            logger.warn("Failed to find order with ID: {}. Reason: {}", orderId, e.getMessage());
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());
            // Return a 404 NOT_FOUND status.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMap);
        }
    }
}
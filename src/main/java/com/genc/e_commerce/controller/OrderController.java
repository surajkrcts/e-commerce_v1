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

@RestController
@CrossOrigin(origins = "*")
public class OrderController {
    private static final Logger logger = LogManager.getLogger(OrderController.class);
    @Autowired
    OrderService orderService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        logger.info("Request received to create order for user ID: {}", orderRequest.getUserId());
        logger.debug("Order creation payload: {}", orderRequest);
        try {
            Order orderDetail = orderService.createOrder(orderRequest);
            OrderResponse orderResponse = new OrderResponse(orderDetail);

            logger.info("Successfully created order with ID: {} for user ID: {}", orderDetail.getOrderId(), orderRequest.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);

        } catch (RuntimeException e) {
            logger.warn("Bad request while creating order for user ID {}: {}", orderRequest.getUserId(), e.getMessage());
            Map<String, Object> resmap = new HashMap<>();
            resmap.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resmap);
        } catch (Exception e) {
            logger.error("Internal server error while creating order for user ID: {}", orderRequest.getUserId(), e);
            Map<String, Object> resmap = new HashMap<>();
            resmap.put("error", "Order creation failed due to server error.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resmap);
        }
    }
    @GetMapping("/order-details/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable Long orderId) {
        logger.info("Request received to get order details for order ID: {}", orderId);
        try {
            Order orderDetail = orderService.getOrderDetails(orderId);
            OrderResponse response = new OrderResponse(orderDetail);
            logger.info("Successfully fetched details for order ID: {}", orderId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.warn("Order not found for ID: {}. Reason: {}", orderId, e.getMessage());
            Map<String, Object> resmap = new HashMap<>();
            resmap.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resmap);
        }
    }
//    @PutMapping("/update-status/{orderId}")
//    public ResponseEntity<?> updateOrderStatus(
//            @PathVariable Long orderId,
//            @RequestBody OrderRequest request) {
//        logger.info("Request received to update status for order ID: {} to {}", orderId, request.getStatus());
//        try {
//
//            Order updatedOrder = orderService.updateOrderStatus(orderId, request.getStatus());
//
//            // Return the updated details using the response DTO
//            OrderResponse response = new OrderResponse(updatedOrder);
//            logger.info("Successfully updated status for order ID: {}", orderId);
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException e) {
//            logger.warn("Failed to update status for order ID: {}. Reason: {}", orderId, e.getMessage());
//            Map<String, Object> resmap = new HashMap<>();
//            resmap.put("error", e.getMessage());
//            // Use NOT_FOUND (404) if the order ID is invalid
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resmap);
//        }
//    }
}

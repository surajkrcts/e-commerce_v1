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
    private OrderService orderService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        logger.info("Request received to create an order for user ID: {}", orderRequest.getUserId());
        logger.debug("Order creation request payload: {}", orderRequest);

        try {
            Order orderDetail = orderService.createOrder(orderRequest);
            OrderResponse orderResponse = new OrderResponse(orderDetail);

            logger.info("Successfully created order with ID: {} for user ID: {}", orderDetail.getOrderId(), orderDetail.getUser().getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);

        } catch (RuntimeException e) {
            logger.warn("Bad request while creating order for user ID {}: {}", orderRequest.getUserId(), e.getMessage());
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);

        } catch (Exception e) {
            logger.error("Internal server error while creating order for user ID: {}", orderRequest.getUserId(), e);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("error", "Order creation failed due to a server error.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }

    @GetMapping("/order-details/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable Long orderId) {
        logger.info("Request received to fetch details for order ID: {}", orderId);
        try {
            Order orderDetail = orderService.getOrderDetails(orderId);
            OrderResponse response = new OrderResponse(orderDetail);
            logger.info("Successfully fetched details for order ID: {}", orderId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.warn("Failed to find order with ID: {}. Reason: {}", orderId, e.getMessage());
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMap);
        }
    }

    /*
    @PutMapping("/update-status/{orderId}")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody OrderRequest request) {
        logger.info("Request to update status for order ID: {} to status: {}", orderId, request.getStatus());
        try {
            Order updatedOrder = orderService.updateOrderStatus(orderId, request.getStatus());
            OrderResponse response = new OrderResponse(updatedOrder);
            logger.info("Successfully updated status for order ID: {}", orderId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.warn("Failed to update status for order ID: {}. Reason: {}", orderId, e.getMessage());
            Map<String, Object> resmap = new HashMap<>();
            resmap.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resmap);
        }
    }
    */
}
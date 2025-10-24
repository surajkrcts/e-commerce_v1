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
        try {
            Order orderDetail = orderService.createOrder(orderRequest);
            OrderResponse orderResponse = new OrderResponse(orderDetail);

            return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);

        } catch (RuntimeException e) {
            Map<String, Object> resmap = new HashMap<>();
            resmap.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resmap);
        } catch (Exception e) {
            Map<String, Object> resmap = new HashMap<>();
            resmap.put("error", "Order creation failed due to server error.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resmap);
        }
    }
    @GetMapping("/order-details/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable Long orderId) {
        try {
            Order orderDetail = orderService.getOrderDetails(orderId);
            OrderResponse response = new OrderResponse(orderDetail);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> resmap = new HashMap<>();
            resmap.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resmap);
        }
    }
//    @PutMapping("/update-status/{orderId}")
//    public ResponseEntity<?> updateOrderStatus(
//            @PathVariable Long orderId,
//            @RequestBody OrderRequest request) {
//        try {
//
//            Order updatedOrder = orderService.updateOrderStatus(orderId, request.getStatus());
//
//            // Return the updated details using the response DTO
//            OrderResponse response = new OrderResponse(updatedOrder);
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException e) {
//            Map<String, Object> resmap = new HashMap<>();
//            resmap.put("error", e.getMessage());
//            // Use NOT_FOUND (404) if the order ID is invalid
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resmap);
//        }
//    }
}


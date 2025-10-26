package com.genc.e_commerce.dto;

import com.genc.e_commerce.entity.Order;
import lombok.Data;

import java.util.Date;

/**
 * Data Transfer Object (DTO) used to carry data from the client
 * to the server when creating or updating an order.
 */
// @Data is a Lombok annotation that automatically generates boilerplate code
// such as getters, setters, toString(), equals(), and hashCode().
@Data
public class OrderRequest {

    // The unique identifier for the user placing the order.
    private Long userId;

    // The total cost of the order.
    private double totalAmount;

    // The date the order was placed. This is often set on the server side.
    private Date orderDate;

    // The status of the order (e.g., PENDING, SHIPPED, CANCELLED).
    private Order.Status status;
}
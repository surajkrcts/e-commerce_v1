package com.genc.e_commerce.dto;

import com.genc.e_commerce.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Data Transfer Object (DTO) used to send a simplified and safe representation
 * of an Order back to the client as a response.
 */
// @NoArgsConstructor is a Lombok annotation that generates a constructor with no arguments.
@NoArgsConstructor
// @AllArgsConstructor is a Lombok annotation that generates a constructor with a parameter for every field.
@AllArgsConstructor
// @Data is a Lombok annotation that bundles features like @Getter, @Setter, @ToString, etc.
@Data
public class OrderResponse {

    // The unique identifier for the order.
    private Long orderId;

    // The total cost of the order.
    private double totalAmount;

    // The date and time when the order was placed.
    private Date orderDate;

    // The current status of the order (e.g., PENDING, SHIPPED).
    private Order.Status status;

    /**
     * A custom constructor that maps an Order entity object to this OrderResponse DTO.
     * This is a convenient way to convert the internal database model (entity)
     * into a format suitable for an API response.
     * @param order The Order entity object from the database.
     */
    public OrderResponse(Order order) {
        // Populates the DTO's fields using data from the provided Order entity.
        this.orderId = order.getOrderId();
        this.totalAmount = order.getTotalAmount();
        this.orderDate = order.getOrderDate();
        this.status = order.getStatus();
    }
}
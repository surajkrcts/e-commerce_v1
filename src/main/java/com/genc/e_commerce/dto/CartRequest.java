package com.genc.e_commerce.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) used to carry data from the client
 * when adding an item to the shopping cart.
 */
// @Data is a Lombok annotation that automatically generates boilerplate code
// such as getters, setters, toString(), equals(), and hashCode().
@Data
public class CartRequest {

    // The unique identifier for the user who owns the cart.
    private Long userId;

    // The unique identifier for the product being added to the cart.
    private Long productId;

    // The number of units of the product to be added.
    private int quantity;
}
package com.genc.e_commerce.dto;

import com.genc.e_commerce.entity.Cart;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object (DTO) used to send a user-friendly representation
 * of a cart item back to the client. It flattens the nested entity structure
 * into a simple JSON object.
 */
// @Data is a Lombok annotation that generates boilerplate code like getters, setters, toString(), etc.
@Data
// @AllArgsConstructor is a Lombok annotation that generates a constructor with a parameter for every field in the class.
@AllArgsConstructor
public class CartResponse {

    // The unique identifier for the cart item itself.
    private Long cartId;

    // The ID of the user who owns the cart.
    private Long userId;

    // The ID of the product in the cart.
    private Long productId;

    // The name of the product, extracted for convenience.
    private String productName;

    // The number of units of this product in the cart.
    private int quantity;

    // The price of a single unit of the product.
    private double unitprice;

    // The total price for this cart item (quantity * unitprice).
    private double itemPriceTotal;

    /**
     * A custom constructor that maps a Cart entity object to this CartResponse DTO.
     * This is a common pattern to easily convert your internal data model (entity)
     * into a format suitable for your API response.
     * @param cart The Cart entity object from the database.
     */
    public CartResponse(Cart cart) {
        // Populates the DTO fields by extracting data from the Cart entity and its related User and Product entities.
        this.cartId = cart.getCartId();
        this.userId = cart.getUser().getUserId();
        this.productId = cart.getProduct().getProductId();
        this.productName = cart.getProduct().getName();
        this.quantity = cart.getQuantity();
        this.unitprice = cart.getProduct().getPrice();
        this.itemPriceTotal = cart.getItemPriceTotal();
    }
}
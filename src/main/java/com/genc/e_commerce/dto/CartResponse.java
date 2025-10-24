package com.genc.e_commerce.dto;

import com.genc.e_commerce.entity.Cart;
import lombok.AllArgsConstructor;
import lombok.Data;



@Data
@AllArgsConstructor

public class CartResponse {
    private Long cartId;
    private Long userId;
    private Long productId;
    private String productName;
    private int quantity;
    private double unitprice;
    private double itemPriceTotal;


    public CartResponse(Cart cart){
        this.cartId=cart.getCartId();
        this.userId=cart.getUser().getUserId();
        this.productId=cart.getProduct().getProductId();
        this.productName=cart.getProduct().getName();
        this.quantity=cart.getQuantity();
        this.unitprice=cart.getProduct().getPrice();
        this.itemPriceTotal=cart.getItemPriceTotal();
    }


}

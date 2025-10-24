package com.genc.e_commerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
//import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;


@Data
@Entity
@Table(name = "cart_item")
public class Cart {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long cartId;
    @NotNull(message = "user must be present for cart item")
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @NotNull(message = "Product msut be specified for cart item")
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @Column(nullable = false)
    private double itemPriceTotal;
    @Min(value = 1, message = "Quantity must not be less than 1")
    @Column(nullable = false)
    @PositiveOrZero
    private int quantity;
}

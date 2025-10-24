package com.genc.e_commerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(name = "prduct_name")
    @Lob
    private String name;

    @NotBlank(message = "product description should not be blank")
    @Lob
    private String description;

    @PositiveOrZero
    private double price;

    @PositiveOrZero
    private int stockQuantity;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "categorty_id")
    private Category category;
//
//    @ManyToOne
//    @JoinColumn(name="user_id")
//    private User user;

}

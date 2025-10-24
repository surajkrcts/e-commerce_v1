package com.genc.e_commerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "Order_T")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @PositiveOrZero
    private double totalAmount;
    private Date orderDate;
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        PENDING, SHIPPED, DELIVERED, CANCELLED
    }
}

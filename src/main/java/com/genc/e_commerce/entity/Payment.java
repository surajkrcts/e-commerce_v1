package com.genc.e_commerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.Date;


@Data
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long paymentId;
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @PositiveOrZero
    private double amount;
    private Date paymentDate;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    private Date createdTimeStamp;
    private Date updatedTimeStamp;

    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED
    }

    public enum PaymentMethod {
        CARD,
        BANK_TRANSFER,
        CASH_ON_DELIVERY,
        UPI
    }

    public Payment(){
        this.paymentStatus=PaymentStatus.PENDING;
        this.createdTimeStamp=new Date();
        this.updatedTimeStamp=createdTimeStamp;
    }

}

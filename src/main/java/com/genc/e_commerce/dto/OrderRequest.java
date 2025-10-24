package com.genc.e_commerce.dto;

import com.genc.e_commerce.entity.Order;
import lombok.Data;

import java.util.Date;
@Data
public class OrderRequest {
    private Long userId;
    private double totalAmount;
    private Date orderDate;
    private Order.Status status;
}

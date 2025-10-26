package com.genc.e_commerce.dto;

import com.genc.e_commerce.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderResponse {
    private Long orderId;
    private double totalAmount;
    private Date orderDate;
    private Order.Status status;


    public OrderResponse(Order order){
        this.orderId = order.getOrderId();
        this.totalAmount= order.getTotalAmount();
        this.orderDate=order.getOrderDate();
        this.status=order.getStatus();
    }

}

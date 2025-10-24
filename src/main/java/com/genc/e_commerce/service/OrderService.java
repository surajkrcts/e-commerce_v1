package com.genc.e_commerce.service;

import com.genc.e_commerce.dto.OrderRequest;
import com.genc.e_commerce.entity.Order;
import com.genc.e_commerce.entity.User;
import com.genc.e_commerce.repository.CartRepo;
import com.genc.e_commerce.repository.OrderRepo;
import com.genc.e_commerce.repository.ProductRepo;
import com.genc.e_commerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepo cartRepo;
    
    @Autowired
    private ProductRepo productRepo;

    @Transactional
    public Order createOrder(OrderRequest orderRequest) {
        Long userId = orderRequest.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("Cannot create order: User not found with ID " + userId));
//        Optional<Order> existingOrderDetails=orderRepo.findByUserUserIdAndStatus(userId,Order.Status.PENDING);
//        if(existingOrderDetails.isPresent()){
//            Order existingOrder=existingOrderDetails.get();
//            return existingOrder;
//        }

        Order newOrder = new Order();
        newOrder.setTotalAmount(orderRequest.getTotalAmount());
        newOrder.setOrderDate(new Date());
        newOrder.setUser(user);

        newOrder.setStatus(Order.Status.PENDING);

        return orderRepo.save(newOrder);
    }

    public Order getOrderDetails(Long orderId) {
        return orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
    }



//    public Order updateOrderStatus(Long orderId, Order.Status newStatus) {
//        Order existingOrder = orderRepo.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
//
//           existingOrder.setStatus(newStatus);
//
//        return orderRepo.save(existingOrder);
//    }
}

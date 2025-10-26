package com.genc.e_commerce.service;

import com.genc.e_commerce.dto.OrderRequest;
import com.genc.e_commerce.entity.Cart;
import com.genc.e_commerce.entity.Order;
import com.genc.e_commerce.entity.User;
import com.genc.e_commerce.exception.ResourceNotFoundException;
import com.genc.e_commerce.repository.CartRepo;
import com.genc.e_commerce.repository.OrderRepo;
import com.genc.e_commerce.repository.ProductRepo;
import com.genc.e_commerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

// @Service annotation marks this class as a Spring service component to handle business logic.
@Service
public class OrderService {

    // @Autowired injects an instance of OrderRepo for database operations related to orders.
    @Autowired
    private OrderRepo orderRepo;

    // @Autowired injects an instance of UserRepository to fetch user data.
    @Autowired
    private UserRepository userRepository;

    // @Autowired injects an instance of CartRepo to interact with the user's cart.
    @Autowired
    private CartRepo cartRepo;

    // @Autowired injects an instance of ProductRepo.
    @Autowired
    private ProductRepo productRepo;

    /**
     * Creates a new order for a user based on their cart items.
     * This method is transactional, ensuring that all database operations are completed successfully or none are.
     * @param orderRequest A DTO containing the user ID and total amount.
     * @return The newly created Order object.
     * @throws RuntimeException if the user is not found.
     * @throws ResourceNotFoundException if the user's cart is empty.
     */
    @Transactional
    public Order createOrder(OrderRequest orderRequest) {
        // Extract the user ID from the incoming request.
        Long userId = orderRequest.getUserId();
        // Find the user by their ID, or throw an exception if the user does not exist.
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("Cannot create order: User not found with ID " + userId));

        // Check if the user already has an order with a 'PENDING' status.
        Optional<Order> existingPendingOrder = orderRepo.findByUserUserIdAndStatus(userId, Order.Status.PENDING);
        // If an existing pending order is found, return it instead of creating a new one.
        if (existingPendingOrder.isPresent()) {
            return existingPendingOrder.get();
        }

        // Get all items from the user's cart.
        List<Cart> cartItems = cartRepo.findByUserUserId(userId);
        // If the cart is empty, throw an exception because an order cannot be created.
        if (cartItems.isEmpty()) {
            throw new ResourceNotFoundException("Cannot create order: Your cart is currently empty.");
        }

        // Create a new Order instance.
        Order newOrder = new Order();
        // Set the order details from the request and the user object.
        newOrder.setTotalAmount(orderRequest.getTotalAmount());
        newOrder.setOrderDate(new Date()); // Set the order date to the current time.
        newOrder.setUser(user);

        // Set the initial status of the new order to 'PENDING'.
        newOrder.setStatus(Order.Status.PENDING);

        // Save the newly created order to the database.
        Order savedOrder = orderRepo.save(newOrder);

        // Return the saved order.
        return savedOrder;
    }

    /**
     * Retrieves the details of a specific order by its ID.
     * @param orderId The ID of the order to retrieve.
     * @return The found Order object.
     * @throws RuntimeException if no order is found with the given ID.
     */
    public Order getOrderDetails(Long orderId) {
        // Find the order by its ID in the database, or throw an exception if it's not found.
        return orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
    }


}
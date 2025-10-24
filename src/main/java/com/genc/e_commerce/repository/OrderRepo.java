package com.genc.e_commerce.repository;

import com.genc.e_commerce.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order,Long> {

    Optional<Order> findByUserUserIdAndStatus(Long userId, Order.Status status);
}

package com.genc.e_commerce.repository;

import com.genc.e_commerce.entity.Cart;
import com.genc.e_commerce.entity.Product;
import com.genc.e_commerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepo extends JpaRepository<Cart,Long> {

    Optional<Cart> findByUserAndProduct(User user, Product product);

    List<Cart> findByUserUserId(Long userId);
}

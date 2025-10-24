package com.genc.e_commerce.repository;

import com.genc.e_commerce.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepo extends JpaRepository<Payment,Long> {
}

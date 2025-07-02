package com.realestate.backend.repository;


import com.realestate.backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository<Payment> extends JpaRepository<com.realestate.backend.model.Payment, Long> {

    List<Payment> findBySellerId(Long sellerId);
}


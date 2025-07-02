package com.realestate.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String customerEmail;
    private Long sellerId;
    private Double amount;
    private String currency;
    private String paymentStatus;

    private LocalDateTime paymentDate;

    // New fields for success and cancel URLs
    private String successUrl;
    private String cancelUrl;


}


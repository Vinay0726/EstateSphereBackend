package com.realestate.backend.controller;

import com.realestate.backend.model.Payment;
import com.realestate.backend.repository.PaymentRepository;
import com.realestate.backend.services.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
//@CrossOrigin(origins = "http://localhost:5173") // Update with your front-end URL
@CrossOrigin(origins = "https://estatesphere.netlify.app/")
public class PaymentController {

    @Autowired
    private StripeService stripeService;

    @Autowired
    private PaymentRepository paymentRepository;

    @PostMapping("/create-checkout-session")
    public String createCheckoutSession(@RequestBody Payment paymentRequest) {
        try {
            // Create a checkout session
            Session session = stripeService.createCheckoutSession(
                    paymentRequest.getAmount(),
                    "inr", // Change to your preferred currency
                    paymentRequest.getSuccessUrl(), // Use successUrl from request body
                    paymentRequest.getCancelUrl()   // Use cancelUrl from request body
            );

            // Save the payment details with a "Pending" status
            paymentRequest.setPaymentStatus("Pending");
            paymentRequest.setPaymentDate(LocalDateTime.now());
            paymentRequest.setSellerId(paymentRequest.getSellerId());
            paymentRequest.setCurrency("inr");
            paymentRepository.save(paymentRequest);

            paymentRequest.setPaymentStatus("Successful");
            paymentRepository.save(paymentRequest);

            return session.getUrl(); // Return the session URL for redirection
        } catch (StripeException e) {
            e.printStackTrace();
            return "Error occurred: " + e.getMessage();
        }
    }

    @GetMapping("/seller/{sellerId}")
    public List<Payment> getPaymentsBySellerId(@PathVariable Long sellerId) {
        return paymentRepository.findBySellerId(sellerId);
    }
}


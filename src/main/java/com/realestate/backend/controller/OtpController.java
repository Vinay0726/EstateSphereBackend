package com.realestate.backend.controller;

import com.realestate.backend.model.OtpVerification;
import com.realestate.backend.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class OtpController {

    private final JavaMailSender mailSender;
    private final OtpRepository otpRepo;

    @PostMapping("/send")
    public ResponseEntity<String> sendOtp(@RequestParam String email) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setEmail(email);
        otpVerification.setOtp(otp);
        otpVerification.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otpVerification.setVerified(false);

        otpRepo.save(otpVerification);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP is: " + otp);
        mailSender.send(message);

        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/verify")
    public ResponseEntity<Boolean> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        OtpVerification record = otpRepo.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (record.getOtp().equals(otp) && record.getExpiryTime().isAfter(LocalDateTime.now())) {
            record.setVerified(true);
            otpRepo.save(record);
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
    }

    @GetMapping("/status")
    public ResponseEntity<Boolean> isVerified(@RequestParam String email) {
        Optional<OtpVerification> record = otpRepo.findTopByEmailOrderByIdDesc(email);
        return ResponseEntity.ok(record.map(OtpVerification::isVerified).orElse(false));
    }
}
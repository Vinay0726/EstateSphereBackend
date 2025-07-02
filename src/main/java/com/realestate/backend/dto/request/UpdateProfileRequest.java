package com.realestate.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password; // You can choose to include this if users can update their password
    private String mobileNumber;
    private String role; // 'Buyer' or 'Seller'

}


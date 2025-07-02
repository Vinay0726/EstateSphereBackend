package com.realestate.backend.services;


import com.realestate.backend.config.JwtProvider;
import com.realestate.backend.dto.request.UpdateProfileRequest;
import com.realestate.backend.dto.response.UserProfileResponse;
import com.realestate.backend.exception.UserException;
import com.realestate.backend.model.User;
import com.realestate.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserProfileResponse findUserProfileByJwt(String jwt) throws UserException {
        String userId = jwtProvider.getIdFromToken(jwt);

        // Fetch user details
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Map User entity to UserProfileResponse with id
        UserProfileResponse userProfileResponse = new UserProfileResponse(
                user.getId(), // Set the user's ID
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getMobileNumber(),
                user.getRole()
        );
        return userProfileResponse;
    }

    public User updateBuyerUserProfile(String jwt, UpdateProfileRequest updateProfileRequest) {
        // Step 1: Extract user ID from JWT
        String userId = jwtProvider.getIdFromToken(jwt);

        System.out.println("user id is...."+userId);

        // Step 2: Find user by ID
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Step 3: Update user details
        user.setFirstName(updateProfileRequest.getFirstName());
        user.setLastName(updateProfileRequest.getLastName());
        user.setEmail(updateProfileRequest.getEmail());
        user.setMobileNumber(updateProfileRequest.getMobileNumber());

        // Encode the password if it was changed
        if (updateProfileRequest.getPassword() != null && !updateProfileRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateProfileRequest.getPassword()));
        }
        user.setRole("BUYER");
        // Step 4: Save the updated user
        return userRepository.save(user);
    }
    public User updateSellerUserProfile(String jwt, UpdateProfileRequest updateProfileRequest) {
        // Step 1: Extract user ID from JWT
        String userId = jwtProvider.getIdFromToken(jwt);

        System.out.println("user id is...."+userId);

        // Step 2: Find user by ID
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Step 3: Update user details
        user.setFirstName(updateProfileRequest.getFirstName());
        user.setLastName(updateProfileRequest.getLastName());
        user.setEmail(updateProfileRequest.getEmail());
        user.setMobileNumber(updateProfileRequest.getMobileNumber());

        // Encode the password if it was changed
        if (updateProfileRequest.getPassword() != null && !updateProfileRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateProfileRequest.getPassword()));
        }
        user.setRole("SELLER");
        // Step 4: Save the updated user
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

}

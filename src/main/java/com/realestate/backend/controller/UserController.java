package com.realestate.backend.controller;

import com.realestate.backend.dto.request.UpdateProfileRequest;
import com.realestate.backend.dto.response.UserProfileResponse;
import com.realestate.backend.exception.UserException;
import com.realestate.backend.model.User;
import com.realestate.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private UserService userService;


    // Endpoint to find user profile by JWT token
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> findUserProfileByJwt(
            @RequestHeader("Authorization") String jwt) throws UserException {
        UserProfileResponse response = userService.findUserProfileByJwt(jwt);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }


    @PutMapping("/buyer/update")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String jwt,
                                        @RequestBody UpdateProfileRequest updateRequest) {
        try {
            // Step 1: Update user details using the service
            User updatedUser = userService.updateBuyerUserProfile(jwt, updateRequest);

            return ResponseEntity.ok("User updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating user and resumes: " + e.getMessage());
        }
    }



    @PutMapping("/seller/update")
    public ResponseEntity<?> updateSellerUser(@RequestHeader("Authorization") String jwt,
                                        @RequestBody UpdateProfileRequest updateRequest) {
        try {
            // Step 1: Update user details using the service
            User updatedUser = userService.updateSellerUserProfile(jwt, updateRequest);

            return ResponseEntity.ok("User updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating user and resumes: " + e.getMessage());
        }
    }

    // Endpoint to get the user's name by ID
    @GetMapping("/{id}/name")
    public ResponseEntity<String> getUserNameById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                String fullName = user.getFirstName() + " " + user.getLastName();
                return ResponseEntity.ok(fullName);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving user name");
        }
    }
//get user details by id
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserDetailsById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                return ResponseEntity.ok(user); // Returns the full user object
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null); // If user is not found
            }
        } catch (Exception e) {
            // Log the exception (for better traceability)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // Or you can send a specific error message
        }
    }






}



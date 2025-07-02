package com.realestate.backend.controller;

import com.realestate.backend.dto.response.PropertyResponse;
import com.realestate.backend.model.Property;
import com.realestate.backend.model.User;
import com.realestate.backend.repository.UserRepository;
import com.realestate.backend.services.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;
    @Autowired
    private UserRepository userRepository;


    @PostMapping("/addProperty")
    public ResponseEntity<String> addProperty(@RequestParam String name,
                                              @RequestParam String propertyType,
                                              @RequestParam String transactionType,
                                              @RequestParam String city,
                                              @RequestParam String address,
                                              @RequestParam String latitude,
                                              @RequestParam String longitude,
                                              @RequestParam double areaSize,
                                              @RequestParam int noOfBhk,
                                              @RequestParam double price,
                                              @RequestParam String description,
                                              @RequestParam String amenities,
                                              @RequestParam("frontImage") MultipartFile frontImage,
                                              @RequestParam("bedImage") MultipartFile bedImage,
                                              @RequestParam("hallImage") MultipartFile hallImage,
                                              @RequestParam("kitchenImage") MultipartFile kitchenImage,
                                              @RequestParam Long sellerId) throws IOException {

        String responseMessage = propertyService.addProperty(name, propertyType, transactionType,city,address, latitude,longitude, areaSize,
                noOfBhk, price, description, amenities, frontImage, bedImage, hallImage, kitchenImage, sellerId);

        return ResponseEntity.ok(responseMessage);
    }

    //for search property
    @GetMapping("/searchProperties")
    public ResponseEntity<Page<PropertyResponse>> searchProperties(
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) Double minAreaSize,
            @RequestParam(required = false) Double maxAreaSize,
            @RequestParam(required = false) Integer noOfBhk,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) List<String> amenities,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PropertyResponse> properties = propertyService.searchProperties(
                propertyType, transactionType, minAreaSize, maxAreaSize,
                noOfBhk, minPrice, maxPrice, amenities, city, page, size);

        return ResponseEntity.ok(properties);
    }

    //get all properties and seller details by property id
    @GetMapping("/{propertyId}")
    public ResponseEntity<PropertyResponse> getPropertyDetails(@PathVariable Long propertyId) {
        PropertyResponse property = propertyService.getPropertyById(propertyId);
        return ResponseEntity.ok(property);
    }

    //get all properties by seller added
    @GetMapping("/seller")
    public ResponseEntity<List<PropertyResponse>> getPropertiesBySeller(Principal principal) {
        // Step 1: Get the seller's email from the Principal
        String sellerEmail = principal.getName();

        // Step 2: Retrieve the seller (User) from the database
        User seller = userRepository.findByEmail(sellerEmail);

        if (seller == null || !seller.getRole().equalsIgnoreCase("Seller")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Return 403 if not a seller
        }

        // Step 3: Fetch all properties added by the seller
        List<PropertyResponse> propertyResponses = propertyService.getPropertiesBySellerResponse(seller);

        // Step 4: Return the properties in the response
        return ResponseEntity.ok(propertyResponses);
    }

    private Long getSellerIdFromUsername(String username) {
        // Fetch the Seller object from the database based on the username or email
        // Example:
        // Seller seller = sellerRepository.findByEmail(username);
        // return seller.getId();
        return 1L; // Replace this with actual logic
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<PropertyResponse>> getAllProperties() {
        List<PropertyResponse> properties = propertyService.getAllProperties();
        return ResponseEntity.ok(properties);
    }

    //delete property by property id
    @DeleteMapping("/{propertyId}")
    public ResponseEntity<?> deleteProperty(@PathVariable Long propertyId) {
        try {
            propertyService.deletePropertyById(propertyId);
            return ResponseEntity.ok("Property deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
package com.realestate.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyResponse {
    private Long id;
    private String name;
    private String propertyType;
    private String transactionType;
    private String city;
    private String address;
    private String latitude;
    private String longitude;
    private Double areaSize;
    private Integer noOfBhk;
    private Double price;
    private List<String> amenities;
    private String frontImage;
    private String bedImage;
    private String hallImage;
    private String kitchenImage;
    private String description;
    private SellerResponse seller;

    // Getters and Setters
}


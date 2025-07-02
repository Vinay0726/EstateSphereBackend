package com.realestate.backend.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "properties")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Name of the property
    private String propertyType; // 'Flat', 'House', 'Plot', 'PG'
    private String transactionType; // 'Buy', 'Rent', 'PG'

    private String city;//Nagpur,Pune,Mumbai,Amravati
    private String address;//In City Exact Area

    private String latitude; // latitude of the property
    private String longitude;//longitude of the property
    private double areaSize; // Size in square feet
    private int noOfBhk; // Number of bedrooms, e.g., 1BHK, 2BHK,3bhk.
    private double price; // Price for buy/rent

    @ElementCollection
    private List<String> amenities; //Amenities such as gym, swimming pool, parking, playground, 24/7 water supply, electricity, Wi-Fi, and CCTV are available

    // Add image names or paths for different sections
    @Lob
    private String frontImage;
    @Lob
    private String bedImage;
    @Lob
    private String hallImage;
    @Lob
    private String kitchenImage;

    private String description; // Description of the property


    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;
}

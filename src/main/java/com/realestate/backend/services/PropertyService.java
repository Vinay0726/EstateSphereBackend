package com.realestate.backend.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.realestate.backend.dto.response.PropertyResponse;
import com.realestate.backend.dto.response.SellerResponse;
import com.realestate.backend.exception.ResourceNotFoundException;
import com.realestate.backend.model.Property;
import com.realestate.backend.model.User;
import com.realestate.backend.repository.PropertyRepository;
import com.realestate.backend.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    private final Cloudinary cloudinary;

    @Autowired
    public PropertyService() {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dxiwp79m4",
                "api_key", "187925137638395",
                "api_secret", "SBRxMN8BPxfrmd1mlxBx-s6hM4w",
                "secure", true));
    }

    public String saveImage(MultipartFile image) throws IOException {
        // Upload image to Cloudinary
        Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.asMap(
                "resource_type", "image",
                "folder", "estatesphere_images"
        ));

        // Return the secure URL of the uploaded image
        return (String) uploadResult.get("secure_url");
    }

    public String addProperty(String name, String propertyType, String transactionType, String city,
                              String address, String latitude, String longitude, double areaSize,
                              int noOfBhk, double price, String description, String amenities,
                              MultipartFile frontImage, MultipartFile bedImage, MultipartFile hallImage,
                              MultipartFile kitchenImage, Long sellerId) throws IOException {

        // Save images to Cloudinary and get their URLs
        String frontImageUrl = saveImage(frontImage);
        String bedImageUrl = saveImage(bedImage);
        String hallImageUrl = saveImage(hallImage);
        String kitchenImageUrl = saveImage(kitchenImage);

        // Convert the string to a List<String>
        List<String> amenitiesList = Arrays.asList(amenities.split("\\s*,\\s*"));

        // Create property object
        Property property = new Property();
        property.setName(name);
        property.setPropertyType(propertyType);
        property.setTransactionType(transactionType);
        property.setCity(city);
        property.setAddress(address);
        property.setLatitude(latitude);
        property.setLongitude(longitude);
        property.setAreaSize(areaSize);
        property.setNoOfBhk(noOfBhk);
        property.setPrice(price);
        property.setDescription(description);
        property.setAmenities(amenitiesList);
        property.setFrontImage(frontImageUrl);
        property.setBedImage(bedImageUrl);
        property.setHallImage(hallImageUrl);
        property.setKitchenImage(kitchenImageUrl);

        // Retrieve seller by sellerId
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found with ID: " + sellerId));

        property.setSeller(seller);

        // Save property to database
        propertyRepository.save(property);

        return "Property added successfully";
    }

    public Page<PropertyResponse> searchProperties(
            String propertyType, String transactionType,
            Double minAreaSize, Double maxAreaSize, Integer noOfBhk,
            Double minPrice, Double maxPrice, List<String> amenities,
            String city, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        // Dynamic filtering using Specification
        Page<Property> properties = propertyRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (propertyType != null && !propertyType.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("propertyType"), propertyType));
            }
            if (transactionType != null && !transactionType.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("transactionType"), transactionType));
            }
            if (minAreaSize != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("areaSize"), minAreaSize));
            }
            if (maxAreaSize != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("areaSize"), maxAreaSize));
            }
            if (noOfBhk != null) {
                predicates.add(criteriaBuilder.equal(root.get("noOfBhk"), noOfBhk));
            }
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            if (city != null && !city.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("city"), "%" + city + "%"));
            }
            if (amenities != null && !amenities.isEmpty()) {
                for (String amenity : amenities) {
                    predicates.add(criteriaBuilder.isMember(amenity, root.get("amenities")));
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);

        // Map to PropertyResponse
        return properties.map(this::mapToPropertyResponse);
    }

    public PropertyResponse getPropertyById(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));

        return mapToPropertyResponse(property);
    }

    private PropertyResponse mapToPropertyResponse(Property property) {
        // Initialize PropertyResponse
        PropertyResponse response = new PropertyResponse();
        response.setId(property.getId());
        response.setName(property.getName());
        response.setPropertyType(property.getPropertyType());
        response.setTransactionType(property.getTransactionType());
        response.setCity(property.getCity());
        response.setAddress(property.getAddress());
        response.setLatitude(property.getLatitude());
        response.setLongitude(property.getLongitude());
        response.setAreaSize(property.getAreaSize());
        response.setNoOfBhk(property.getNoOfBhk());
        response.setPrice(property.getPrice());
        response.setAmenities(property.getAmenities());

        // Use Cloudinary URLs directly
        response.setFrontImage(property.getFrontImage());
        response.setBedImage(property.getBedImage());
        response.setHallImage(property.getHallImage());
        response.setKitchenImage(property.getKitchenImage());

        response.setDescription(property.getDescription());

        // Map seller details
        SellerResponse seller = new SellerResponse();
        seller.setId(property.getSeller().getId());
        seller.setFirstName(property.getSeller().getFirstName());
        seller.setLastName(property.getSeller().getLastName());
        seller.setEmail(property.getSeller().getEmail());
        seller.setMobileNumber(property.getSeller().getMobileNumber());
        seller.setRole(property.getSeller().getRole());

        response.setSeller(seller);

        return response;
    }

    public List<PropertyResponse> getPropertiesBySellerResponse(User seller) {
        // Fetch properties from the repository
        List<Property> properties = propertyRepository.findBySellerId(seller.getId());

        // Map properties to PropertyResponse DTO
        return properties.stream().map(property -> {
            SellerResponse sellerResponse = new SellerResponse(
                    seller.getId(),
                    seller.getFirstName(),
                    seller.getLastName(),
                    seller.getEmail(),
                    seller.getMobileNumber(),
                    seller.getRole()
            );

            return new PropertyResponse(
                    property.getId(),
                    property.getName(),
                    property.getPropertyType(),
                    property.getTransactionType(),
                    property.getCity(),
                    property.getAddress(),
                    property.getLatitude(),
                    property.getLongitude(),
                    property.getAreaSize(),
                    property.getNoOfBhk(),
                    property.getPrice(),
                    property.getAmenities(),
                    property.getFrontImage(),  // Use Cloudinary URL
                    property.getBedImage(),
                    property.getHallImage(),
                    property.getKitchenImage(),
                    property.getDescription(),
                    sellerResponse
            );
        }).toList();
    }

    public List<PropertyResponse> getAllProperties() {
        // Fetch all properties from the repository
        List<Property> properties = propertyRepository.findAll();

        // Map properties to PropertyResponse DTO
        return properties.stream().map(property -> {
            // Assuming the seller is already associated with the property
            SellerResponse sellerResponse = new SellerResponse(
                    property.getSeller().getId(),
                    property.getSeller().getFirstName(),
                    property.getSeller().getLastName(),
                    property.getSeller().getEmail(),
                    property.getSeller().getMobileNumber(),
                    property.getSeller().getRole()
            );

            return new PropertyResponse(
                    property.getId(),
                    property.getName(),
                    property.getPropertyType(),
                    property.getTransactionType(),
                    property.getCity(),
                    property.getAddress(),
                    property.getLatitude(),
                    property.getLongitude(),
                    property.getAreaSize(),
                    property.getNoOfBhk(),
                    property.getPrice(),
                    property.getAmenities(),
                    property.getFrontImage(),  // Use Cloudinary URL
                    property.getBedImage(),
                    property.getHallImage(),
                    property.getKitchenImage(),
                    property.getDescription(),
                    sellerResponse
            );
        }).toList();
    }

    public void deletePropertyById(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property with ID " + propertyId + " does not exist."));

        // Extract public IDs from Cloudinary URLs and delete images
        try {
            deleteImageFromCloudinary(property.getFrontImage());
            deleteImageFromCloudinary(property.getBedImage());
            deleteImageFromCloudinary(property.getHallImage());
            deleteImageFromCloudinary(property.getKitchenImage());
        } catch (Exception e) {
            // Log error but don't block deletion
            System.err.println("Failed to delete images from Cloudinary: " + e.getMessage());
        }

        // Delete property from database
        propertyRepository.deleteById(propertyId);
    }

    private void deleteImageFromCloudinary(String imageUrl) throws Exception {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Extract public ID from Cloudinary URL
            String publicId = extractPublicIdFromUrl(imageUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
            }
        }
    }

    private String extractPublicIdFromUrl(String imageUrl) {
        // Example URL: https://res.cloudinary.com/my_cloud_name/image/upload/v1234567890/estatesphere_images/image_id.jpg
        if (imageUrl == null || imageUrl.isEmpty()) return null;
        String[] parts = imageUrl.split("/");
        for (int i = 0; i < parts.length; i++) {
            if ("upload".equals(parts[i]) && i + 2 < parts.length) {
                // The public ID includes the folder and file name (e.g., estatesphere_images/image_id)
                String publicId = parts[i + 2];
                if (publicId.contains(".")) {
                    publicId = publicId.substring(0, publicId.lastIndexOf(".")); // Remove extension
                }
                return publicId;
            }
        }
        return null;
    }
}
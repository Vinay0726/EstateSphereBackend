package com.realestate.backend.services;

import com.realestate.backend.exception.ResourceNotFoundException;
import com.realestate.backend.model.Favorite;
import com.realestate.backend.model.Property;
import com.realestate.backend.model.User;
import com.realestate.backend.repository.FavoriteRepository;
import com.realestate.backend.repository.PropertyRepository;
import com.realestate.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository; // Assuming you have a UserRepository

    @Autowired
    private PropertyRepository propertyRepository; // Assuming you have a PropertyRepository

    public Favorite addFavorite(Long userId, Long propertyId) {
        // Retrieve user and property
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + propertyId));

        // Create and save favorite
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProperty(property);
        return favoriteRepository.save(favorite); // Return the saved favorite
    }

    public void removeFavorite(Long userId, Long propertyId) {
        // Remove favorite
        if (!favoriteRepository.existsByUserIdAndPropertyId(userId, propertyId)) {
            throw new RuntimeException("Favorite not found");
        }
        favoriteRepository.deleteByUserIdAndPropertyId(userId, propertyId);
    }

    public List<Favorite> getFavorites(Long userId) {
        // Fetch all favorites for the user
        return favoriteRepository.findByUserId(userId);
    }
}

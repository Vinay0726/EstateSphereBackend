package com.realestate.backend.repository;

import com.realestate.backend.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // Find all favorites for a specific user
    List<Favorite> findByUserId(Long userId);

    // Check if a property is already favorited by a user
    boolean existsByUserIdAndPropertyId(Long userId, Long propertyId);

    // Delete a favorite by user and property
    void deleteByUserIdAndPropertyId(Long userId, Long propertyId);
}

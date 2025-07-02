package com.realestate.backend.controller;

import com.realestate.backend.dto.request.FavoriteRequest;
import com.realestate.backend.model.Favorite;
import com.realestate.backend.services.FavoriteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    // Add a property to favorites
    @PostMapping
    public ResponseEntity<Favorite> addToFavorites(@RequestBody FavoriteRequest favoriteRequest) {
        Favorite favorite = favoriteService.addFavorite(favoriteRequest.getUserId(), favoriteRequest.getPropertyId());
        return ResponseEntity.ok(favorite);
    }

    // Remove a property from favorites
    @Transactional
    @DeleteMapping
    public ResponseEntity<String> removeFavorite(@RequestParam Long userId, @RequestParam Long propertyId) {
        favoriteService.removeFavorite(userId, propertyId);
        return ResponseEntity.ok("Property removed from favorites");
    }

    // Get all favorite properties of a user
    @GetMapping
    public ResponseEntity<List<Favorite>> getFavorites(@RequestParam Long userId) {
        List<Favorite> favorites = favoriteService.getFavorites(userId);
        return ResponseEntity.ok(favorites);
    }
}

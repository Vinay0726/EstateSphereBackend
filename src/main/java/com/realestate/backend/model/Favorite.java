package com.realestate.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // The user who added the favorite

    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property; // The property that is favorited

    // New Constructor
    public Favorite(User user, Property property) {
        this.user = user;
        this.property = property;
    }



}

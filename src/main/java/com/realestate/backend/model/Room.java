package com.realestate.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremented primary key
    private Long id; // Primary key for the room

    private String roomId;

    private Long buyerId; // ID of the buyer
    private Long propertyId; // ID of the property
    private Long sellerId; // ID of the seller

    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER)
    private Set<Message> messages;


}
package com.realestate.backend.repository;


import com.realestate.backend.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

    public interface RoomRepository extends JpaRepository<Room,Long> {
    //get room using room id
    Optional<Room> findByRoomId(String roomId);

        Optional<Room> findByBuyerIdAndPropertyIdAndSellerId(Long buyerId, Long propertyId, Long sellerId);

        // Fetch all rooms by sellerId
        List<Room> findBySellerId(Long sellerId);
}
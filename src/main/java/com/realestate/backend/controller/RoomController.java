package com.realestate.backend.controller;

import com.realestate.backend.dto.response.MessageDTO;
import com.realestate.backend.dto.response.RoomDTO;
import com.realestate.backend.dto.response.RoomResponse;
import com.realestate.backend.model.Message;
import com.realestate.backend.model.Room;
import com.realestate.backend.repository.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/rooms")
//@CrossOrigin("http://localhost:5173")
@CrossOrigin("https://estatesphere.netlify.app/")
public class RoomController {

    private final RoomRepository roomRepository;

    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // Create a new room
    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody Map<String, Long> roomRequest) {
        Long buyerId = roomRequest.get("buyerId");
        Long propertyId = roomRequest.get("propertyId");
        Long sellerId = roomRequest.get("sellerId");

        if (buyerId == null || propertyId == null || sellerId == null) {
            return ResponseEntity.badRequest().body("Buyer ID, Property ID, and Seller ID are required!");
        }

        // Generate roomId based on buyerId, propertyId, and sellerId
        String roomId = "Room" + buyerId + propertyId + sellerId;

        // Check if room already exists
        if (roomRepository.findByRoomId(roomId).isPresent()) {
            return ResponseEntity.badRequest().body("Room already exists!");
        }

        // Create new room
        Room room = new Room();
        room.setRoomId(roomId);
        room.setBuyerId(buyerId);
        room.setPropertyId(propertyId);
        room.setSellerId(sellerId);
        Room savedRoom = roomRepository.save(room);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoom);
    }

    @GetMapping("/getRoomId")
    public ResponseEntity<?> getRoomId(@RequestParam Long buyerId,
                                       @RequestParam Long propertyId,
                                       @RequestParam Long sellerId) {
        if (buyerId == null || propertyId == null || sellerId == null) {
            return ResponseEntity.badRequest().body("Buyer ID, Property ID, and Seller ID are required!");
        }

        Optional<Room> roomOptional = roomRepository.findByBuyerIdAndPropertyIdAndSellerId(buyerId, propertyId, sellerId);

        return roomOptional.<ResponseEntity<?>>map(room -> ResponseEntity.ok(room.getRoomId())).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found!"));
    }
//    get all room according to seller id
@GetMapping("/getRoomsBySellerId")
public ResponseEntity<?> getRoomsBySellerId(@RequestParam Long sellerId) {
    if (sellerId == null) {
        return ResponseEntity.badRequest().body("Seller ID is required!");
    }

    List<Room> rooms = roomRepository.findBySellerId(sellerId);

    if (rooms.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No rooms found for the given seller ID!");
    }

    List<RoomResponse> roomResponses = rooms.stream()
            .map(this::mapToRoomResponse) // Use the mapping method
            .collect(Collectors.toList());

    return ResponseEntity.ok(roomResponses);
}

    // Mapping method to convert Room entity to RoomResponse DTO
    private RoomResponse mapToRoomResponse(Room room) {
        return new RoomResponse(
                room.getRoomId(),
                room.getPropertyId(),
                room.getSellerId(),
                room.getBuyerId()

        );
    }






    // Join a room
    @GetMapping("/{roomId}")
    public ResponseEntity<?> joinRoom(@PathVariable String roomId) {
        Optional<Room> optionalRoom = roomRepository.findByRoomId(roomId);

        if (optionalRoom.isEmpty()) {
            return ResponseEntity.badRequest().body("Room not found!");
        }

        Room room = optionalRoom.get();

        // Map Room entity to RoomResponseDTO
        List<MessageDTO> messageDTOs = room.getMessages().stream()
                .map(message -> new MessageDTO(
                        message.getSender(),
                        message.getContent(),
                        message.getTimeStamp()
                ))
                .collect(Collectors.toList());

        RoomDTO roomResponseDTO = new RoomDTO(room.getRoomId(), messageDTOs);

        return ResponseEntity.ok(roomResponseDTO);
    }



    // Get messages of a room with pagination
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<?> getMessages(
            @PathVariable String roomId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        Optional<Room> optionalRoom = roomRepository.findByRoomId(roomId);

        if (optionalRoom.isEmpty()) {
            return ResponseEntity.badRequest().body("Room not found!");
        }

        Room room = optionalRoom.get();

        // Sort messages by timestamp in descending order
        List<Message> messages = room.getMessages()
                .stream()
                .sorted((m1, m2) -> m2.getTimeStamp().compareTo(m1.getTimeStamp()))
                .toList();

        int totalMessages = messages.size();
        int start = Math.min(page * size, totalMessages);
        int end = Math.min(start + size, totalMessages);

        // Paginate and map to DTOs
        List<MessageDTO> paginatedMessages = messages.subList(start, end).stream()
                .map(message -> new MessageDTO(
                        message.getSender(),
                        message.getContent(),
                        message.getTimeStamp()
                ))
                .collect(Collectors.toList());

        // Create RoomDTO
        RoomDTO roomDTO = new RoomDTO(room.getRoomId(), paginatedMessages);

        // Create a Page object for metadata
        Page<MessageDTO> messagePage = new PageImpl<>(
                paginatedMessages,
                PageRequest.of(page, size),
                totalMessages
        );

        return ResponseEntity.ok(messagePage);
    }
}

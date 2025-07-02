package com.realestate.backend.controller;



import com.realestate.backend.dto.request.MessageRequest;
import com.realestate.backend.model.Message;
import com.realestate.backend.model.Room;
import com.realestate.backend.repository.MessageRepository;
import com.realestate.backend.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@CrossOrigin("http://localhost:5173")
public class ChatController {

    private final RoomRepository roomRepository;

    public ChatController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Autowired
    private MessageRepository messageRepository;

    @MessageMapping("/sendMessage/{roomId}")
    @SendTo("/topic/room/{roomId}")
    @Transactional
    public Message sendMessage(
            @DestinationVariable String roomId,
            MessageRequest request
    ) {
        // Retrieve room using JPA repository
        Room room = roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found!"));

        // Create new message
        Message message = new Message(request.getSender(), request.getContent());

        // Associate message with room
        message.setRoom(room);  // Ensure the room is set correctly

        // Add message to room's message list (optional if you want to update the list in the room)
        room.getMessages().add(message);

        // Save the message in the database
        messageRepository.save(message);

        // Save the updated room (JPA will also persist the messages)
        roomRepository.save(room);

        return message;
    }

}

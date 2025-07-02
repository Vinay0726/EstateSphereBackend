package com.realestate.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // For broadcasting messages to subscribers
        config.setApplicationDestinationPrefixes("/app"); // Prefix for messages sent to the server
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat") // Endpoint for WebSocket connection
//                .setAllowedOrigins("http://localhost:5173") // Allow requests from frontend
                .setAllowedOrigins("https://estatesphere.netlify.app/") // Allow requests from frontend
                .withSockJS(); // Fallback to SockJS for older browsers
    }
}


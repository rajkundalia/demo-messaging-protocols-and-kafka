package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.MessageDto;
import org.example.stomp.StompService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@Slf4j
public class StompController {
    
    private final StompService stompService;
    
    public StompController(StompService stompService) {
        this.stompService = stompService;
    }
    
    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public MessageDto handleMessage(String message) {
        log.info("WebSocket received message: {}", message);
        
        MessageDto dto = MessageDto.builder()
                .protocol("STOMP")
                .content(message)
                .timestamp(LocalDateTime.now().toString())
                .sender("WebSocket Client")
                .build();
        
        // Add to service's message list
        stompService.addReceivedMessage(dto);
        
        log.info("Broadcasting WebSocket message: {}", dto);
        return dto;
    }
}
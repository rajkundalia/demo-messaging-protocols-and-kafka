package org.example.stomp;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.MessageDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StompService {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final List<MessageDto> receivedMessages = new ArrayList<>();
    
    public StompService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    public void sendMessage(String message) {
        MessageDto dto = MessageDto.builder()
                .protocol("STOMP")
                .content(message)
                .timestamp(LocalDateTime.now().toString())
                .sender("Web Client")
                .build();
        
        messagingTemplate.convertAndSend("/topic/messages", dto);
        log.info("STOMP message sent: {}", message);
    }
    
    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public MessageDto handleMessage(String message) {
        MessageDto dto = MessageDto.builder()
                .protocol("STOMP")
                .content(message)
                .timestamp(LocalDateTime.now().toString())
                .sender("WebSocket Client")
                .build();
        
        receivedMessages.add(dto);
        log.info("STOMP message processed: {}", message);
        return dto;
    }
    
    public List<MessageDto> getReceivedMessages() {
        return new ArrayList<>(receivedMessages);
    }
}
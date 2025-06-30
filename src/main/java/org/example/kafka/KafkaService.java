package org.example.kafka;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.MessageDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class KafkaService {
    
    private final KafkaTemplate<String, MessageDto> kafkaTemplate;
    private final List<MessageDto> receivedMessages = new ArrayList<>();
    
    public KafkaService(KafkaTemplate<String, MessageDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    public void sendMessage(String message) {
        MessageDto dto = MessageDto.builder()
                .protocol("Kafka")
                .content(message)
                .timestamp(LocalDateTime.now().toString())
                .sender("Event Producer")
                .build();
        
        kafkaTemplate.send("demo-topic", dto);
        log.info("Kafka message sent: {}", message);
    }
    
    @KafkaListener(topics = "demo-topic", groupId = "demo-group")
    public void receiveMessage(MessageDto message) {
        receivedMessages.add(message);
        log.info("Kafka message received: {}", message.getContent());
    }
    
    public List<MessageDto> getReceivedMessages() {
        return new ArrayList<>(receivedMessages);
    }
}
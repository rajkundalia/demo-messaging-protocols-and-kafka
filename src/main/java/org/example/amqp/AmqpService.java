package org.example.amqp;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.MessageDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AmqpService {
    
    private final RabbitTemplate rabbitTemplate;
    private final List<MessageDto> receivedMessages = new ArrayList<>();
    
    public AmqpService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /*
    Exchange: A message router that receives messages from producers and forwards them to queues based on defined rules.
    Queue: A storage buffer that holds messages until a consumer is ready to process them.
     */

    public void sendMessage(String message) {
        MessageDto dto = MessageDto.builder()
                .protocol("AMQP")
                .content(message)
                .timestamp(LocalDateTime.now().toString())
                .sender("Producer")
                .build();
        
        rabbitTemplate.convertAndSend("demo.exchange", "demo.routing.key", dto);
        log.info("AMQP message sent: {}", message);
    }
    
    @RabbitListener(queues = "demo.queue")
    public void receiveMessage(MessageDto message) {
        receivedMessages.add(message);
        log.info("AMQP message received: {}", message.getContent());
    }
    
    public List<MessageDto> getReceivedMessages() {
        return new ArrayList<>(receivedMessages);
    }
}
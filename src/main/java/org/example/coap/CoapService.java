package org.example.coap;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.elements.config.Configuration;
import org.example.dto.MessageDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CoapService extends CoapResource {
    
    private final List<MessageDto> receivedMessages = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private CoapServer server;
    
    public CoapService() {
        super("messages");
        getAttributes().setTitle("CoAP Message Resource");
    }
    
    @PostConstruct
    public void initialize() {
        CoapConfig.register();

        // Initialize standard configuration
        Configuration config = Configuration.getStandard();

        server = new CoapServer(config, 5683);
        server.add(this);
        server.start();
        log.info("CoAP server started on port 5683");
    }

    @PreDestroy
    public void stop() {
        if (server != null) server.destroy();
    }
    
    @Override
    public void handlePOST(CoapExchange exchange) {
        try {
            String payload = exchange.getRequestText();
            MessageDto dto = objectMapper.readValue(payload, MessageDto.class);
            receivedMessages.add(dto);
            
            log.info("CoAP message received: {}", dto.getContent());
            exchange.respond(CoAP.ResponseCode.CREATED, "Message received");
        } catch (Exception e) {
            log.error("CoAP POST handling failed: {}", e.getMessage());
            exchange.respond(CoAP.ResponseCode.BAD_REQUEST, "Invalid message format");
        }
    }
    
    @Override
    public void handleGET(CoapExchange exchange) {
        try {
            String json = objectMapper.writeValueAsString(receivedMessages);
            exchange.respond(CoAP.ResponseCode.CONTENT, json, MediaTypeRegistry.APPLICATION_JSON);
        } catch (Exception e) {
            log.error("CoAP GET handling failed: {}", e.getMessage());
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
    
    public void sendMessage(String message) {
        try {
            MessageDto dto = MessageDto.builder()
                    .protocol("CoAP")
                    .content(message)
                    .timestamp(LocalDateTime.now().toString())
                    .sender("IoT Sensor")
                    .build();
            
            CoapClient client = new CoapClient("coap://localhost:5683/messages");
            String json = objectMapper.writeValueAsString(dto);
            CoapResponse response = client.post(json, MediaTypeRegistry.APPLICATION_JSON);
            
            if (response != null) {
                log.info("CoAP message sent: {}", message);
            }
            client.shutdown();
        } catch (Exception e) {
            log.error("CoAP send failed: {}", e.getMessage());
        }
    }
    
    public List<MessageDto> getReceivedMessages() {
        return new ArrayList<>(receivedMessages);
    }
}
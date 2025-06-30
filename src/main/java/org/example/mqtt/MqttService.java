package org.example.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.example.dto.MessageDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MqttService implements MqttCallback {

    private MqttClient client;
    private final List<MessageDto> receivedMessages = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void initialize() {
        try {
            client = new MqttClient("tcp://localhost:1883", "spring-boot-client");
            client.setCallback(this);
            client.connect();
            client.subscribe("demo/topic");
            log.info("MQTT client connected and subscribed");
        } catch (Exception e) {
            log.error("MQTT connection failed: {}", e.getMessage());
        }
    }

    public void sendMessage(String message) {
        try {
            MessageDto dto = MessageDto.builder()
                    .protocol("MQTT")
                    .content(message)
                    .timestamp(LocalDateTime.now().toString())
                    .sender("IoT Device")
                    .build();

            String json = objectMapper.writeValueAsString(dto);
            MqttMessage mqttMessage = new MqttMessage(json.getBytes());
            // Sets the Quality of Service (QoS) level to 1 (at least once).
            mqttMessage.setQos(1);

            client.publish("demo/topic", mqttMessage);
            log.info("MQTT message published: {}", message);
        } catch (Exception e) {
            log.error("MQTT publish failed: {}", e.getMessage());
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        MessageDto dto = objectMapper.readValue(payload, MessageDto.class);
        receivedMessages.add(dto);
        log.info("MQTT message received: {}", dto.getContent());
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.warn("MQTT connection lost: {}", cause.getMessage());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        log.debug("MQTT delivery complete");
    }

    public List<MessageDto> getReceivedMessages() {
        return new ArrayList<>(receivedMessages);
    }
}
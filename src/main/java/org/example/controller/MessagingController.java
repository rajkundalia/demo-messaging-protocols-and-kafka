package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.amqp.AmqpService;
import org.example.coap.CoapService;
import org.example.dto.MessageDto;
import org.example.kafka.KafkaService;
import org.example.mqtt.MqttService;
import org.example.stomp.StompService;
import org.example.xmpp.XmppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messaging")
@Slf4j
public class MessagingController {

    private final AmqpService amqpService;
    private final MqttService mqttService;
    private final StompService stompService;
    private final CoapService coapService;
    private final XmppService xmppService;
    private final KafkaService kafkaService;

    public MessagingController(AmqpService amqpService, MqttService mqttService,
                               StompService stompService, CoapService coapService,
                               XmppService xmppService, KafkaService kafkaService) {
        this.amqpService = amqpService;
        this.mqttService = mqttService;
        this.stompService = stompService;
        this.coapService = coapService;
        this.xmppService = xmppService;
        this.kafkaService = kafkaService;
    }

    @PostMapping("/amqp/send")
    public ResponseEntity<String> sendAmqpMessage(@RequestBody Map<String, String> request) {
        amqpService.sendMessage(request.get("message"));
        return ResponseEntity.ok("AMQP message sent");
    }

    @PostMapping("/mqtt/send")
    public ResponseEntity<String> sendMqttMessage(@RequestBody Map<String, String> request) {
        mqttService.sendMessage(request.get("message"));
        return ResponseEntity.ok("MQTT message sent");
    }

    @PostMapping("/stomp/send")
    public ResponseEntity<String> sendStompMessage(@RequestBody Map<String, String> request) {
        stompService.sendMessage(request.get("message"));
        return ResponseEntity.ok("STOMP message sent");
    }

    @PostMapping("/coap/send")
    public ResponseEntity<String> sendCoapMessage(@RequestBody Map<String, String> request) {
        coapService.sendMessage(request.get("message"));
        return ResponseEntity.ok("CoAP message sent");
    }

    @PostMapping("/xmpp/send")
    public ResponseEntity<String> sendXmppMessage(@RequestBody Map<String, String> request) {
        xmppService.sendMessage(request.get("message"));
        return ResponseEntity.ok("XMPP message sent");
    }

    @PostMapping("/kafka/send")
    public ResponseEntity<String> sendKafkaMessage(@RequestBody Map<String, String> request) {
        kafkaService.sendMessage(request.get("message"));
        return ResponseEntity.ok("Kafka message sent");
    }

    @GetMapping("/messages")
    public ResponseEntity<Map<String, List<MessageDto>>> getAllMessages() {
        Map<String, List<MessageDto>> allMessages = Map.of(
                "amqp", amqpService.getReceivedMessages(),
                "mqtt", mqttService.getReceivedMessages(),
                "stomp", stompService.getReceivedMessages(),
                "coap", coapService.getReceivedMessages(),
                "xmpp", xmppService.getReceivedMessages(),
                "kafka", kafkaService.getReceivedMessages()
        );
        return ResponseEntity.ok(allMessages);
    }
}
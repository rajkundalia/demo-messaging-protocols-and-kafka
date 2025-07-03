package org.example.xmpp;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.MessageDto;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class XmppService {

    private XMPPTCPConnection connection;
    private final List<MessageDto> receivedMessages = new ArrayList<>();

    @PostConstruct
    public void initialize() {
        try {

            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setUsernameAndPassword("testuser", "testpass")
                    .setXmppDomain("localhost")
                    .setHost("127.0.0.1")
                    .setPort(5222)
                    .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled)
                    .setSendPresence(true)
                    .setCompressionEnabled(false)
                    .build();

            connection = new XMPPTCPConnection(config);

            log.info("Attempting to connect to XMPP server...");
            connection.connect();

            log.info("Connected to XMPP server, attempting login...");
            connection.login();

            ChatManager chatManager = ChatManager.getInstanceFor(connection);
            chatManager.addIncomingListener((from, message, chat) -> {
                MessageDto dto = MessageDto.builder()
                        .protocol("XMPP")
                        .content(message.getBody())
                        .timestamp(LocalDateTime.now().toString())
                        .sender(from.toString())
                        .build();

                receivedMessages.add(dto);
                log.info("XMPP message received: {}", message.getBody());
            });

            log.info("XMPP client connected and logged in");
        } catch (Exception e) {
            log.error("XMPP connection failed: {}", e.getMessage(), e);
        }
    }

    /**
     * Initialize Base64 encoder to fix the null pointer exception
     */
//    private void initializeBase64Encoder() {
//        try {
//            // Set the Base64 encoder manually
//            Base64.setEncoder();
//            Base64UrlSafeEncoder.setEncoder();
//            log.info("Base64 encoder initialized successfully");
//        } catch (Exception e) {
//            log.warn("Failed to initialize Base64 encoder: {}", e.getMessage());
//        }
//    }

    public void sendMessage(String message, String recipient) {
        try {
            if (connection == null || !connection.isConnected() || !connection.isAuthenticated()) {
                log.warn("XMPP connection not established or authenticated. Attempting to re-initialize.");
                initialize(); // Attempt to re-initialize if not connected
                if (connection == null || !connection.isConnected() || !connection.isAuthenticated()) {
                    log.error("Failed to establish XMPP connection for sending message.");
                    return;
                }
            }

            ChatManager chatManager = ChatManager.getInstanceFor(connection);
            EntityBareJid jid = JidCreate.entityBareFrom(recipient + "@localhost");
            Chat chat = chatManager.chatWith(jid);

            chat.send(message);
            log.info("XMPP message sent to {}: {}", recipient, message);
        } catch (Exception e) {
            log.error("XMPP send failed: {}", e.getMessage(), e);
        }
    }

    public void sendMessage(String message) {
        sendMessage(message, "testuser");
    }

    public List<MessageDto> getReceivedMessages() {
        return new ArrayList<>(receivedMessages);
    }
}
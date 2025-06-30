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
                    .setHost("localhost")
                    .setPort(5222)
                    .build();

            connection = new XMPPTCPConnection(config);
            connection.connect();
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
            log.error("XMPP connection failed: {}", e.getMessage());
        }
    }

    public void sendMessage(String message, String recipient) {
        try {
            ChatManager chatManager = ChatManager.getInstanceFor(connection);
            EntityBareJid jid = JidCreate.entityBareFrom(recipient + "@localhost");
            Chat chat = chatManager.chatWith(jid);

            chat.send(message);
            log.info("XMPP message sent to {}: {}", recipient, message);
        } catch (Exception e) {
            log.error("XMPP send failed: {}", e.getMessage());
        }
    }

    public void sendMessage(String message) {
        sendMessage(message, "recipient");
    }

    public List<MessageDto> getReceivedMessages() {
        return new ArrayList<>(receivedMessages);
    }
}
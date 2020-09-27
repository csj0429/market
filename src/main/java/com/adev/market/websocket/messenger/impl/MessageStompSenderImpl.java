package com.adev.market.websocket.messenger.impl;

import com.adev.market.websocket.messenger.MessageStompSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class MessageStompSenderImpl implements MessageStompSender {

    private static final Logger LOG = LoggerFactory.getLogger(MessageStompSenderImpl.class);

    private final SimpMessagingTemplate websocket;

    private final ObjectMapper objectMapper;

    @Autowired
    public MessageStompSenderImpl(SimpMessagingTemplate websocket, ObjectMapper objectMapper) {
        this.websocket = websocket;
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendMessage(String message) {
        try {
            sendMessage(String.valueOf(objectMapper.readValue(message, Map.class).get("channel")), message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String channel, Object payload) {
        websocket.convertAndSend(channel, payload);
        LOG.debug("Sent message to [{}] using STOMP.", channel);
    }
}

package com.adev.market.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MessageHandler extends TextWebSocketHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MessageHandler.class);

    @Resource
    private ObjectMapper objectMapper;

    private static ObjectMapper staticObjectMapper;

    @Resource
    private MessageHandlerStorage messageHandlerStorage;

    private static MessageHandlerStorage staticMessageHandlerStorage;

    @PostConstruct
    public void init() {
        staticObjectMapper = objectMapper;
        staticMessageHandlerStorage = messageHandlerStorage;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        messageHandlerStorage.addSession(session);
        LOG.info("Open a new connection, current onlineCount: {}", messageHandlerStorage.getSessions().size());
        sendMessage(session, new TextMessage("Welcome!"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        messageHandlerStorage.removeSession(session);
        LOG.info("Close a connection, current onlineCount: {}", messageHandlerStorage.getSessions().size());
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) {
        LOG.debug("Coming! Pong!");
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        LOG.info("New client message coming: " + message.getPayload()+",current onlineCount: {}", messageHandlerStorage.getSessions().size());

        if (message.getPayload().equalsIgnoreCase("ping")) {
            sendMessage(session, new TextMessage("PONG"));
            return;
        }

        try {
            Map map = objectMapper.readValue(message.getPayload(), Map.class);
            String type = String.valueOf(map.get("type"));
            String data = String.valueOf(map.get("data"));

            switch (type.toLowerCase()) {
                case "channel":
                    messageHandlerStorage.addChannel(data, session);
                    break;
                case "channeloff":
                    messageHandlerStorage.removeChannel(data, session);
                    break;
                case "subscribe":
                    messageHandlerStorage.addChannel(data, session);
                    break;
                case "unsubscribe":
                    messageHandlerStorage.removeChannel(data, session);
                    break;
            }
        } catch (IOException e) {
            sendMessage(session, new TextMessage("Wrong Message! (Must be a JSONString)"));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        LOG.info("Something wrong! the session: {}", session);
        exception.printStackTrace();
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * Send message.
     *
     * @param session session
     * @param message message
     */
    public void sendMessage(WebSocketSession session, TextMessage message) {
        try {
            session.sendMessage(message);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Group send message.
     *
     * @param message JSONString (must include 'channel' property)
     */
    public static void groupSendMessage(String message) {
        try {
            if(null!=staticObjectMapper){
                String channel=String.valueOf(staticObjectMapper.readValue(message, Map.class).get("channel"));
                groupSendMessage(channel, message);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Group send message.
     *
     * @param message message
     *  TODO 之前有添加 synchronized，暂时去掉
     */
    public static void groupSendMessage(String channel, String message) {
        if(null==staticMessageHandlerStorage){
            return;
        }
        Set<String> sessionIdSet=staticMessageHandlerStorage.getSessionIdSet(channel);
        if(null!=sessionIdSet&&!sessionIdSet.isEmpty()){
            Iterator<String> iterator=sessionIdSet.iterator();
            while (iterator.hasNext()){
                String sessionId=iterator.next();
                WebSocketSession session=staticMessageHandlerStorage.getSession(sessionId);
                if(null!=session){
                    try {
                        synchronized (session){
                            session.sendMessage(new TextMessage(message));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    iterator.remove();
                }
            }
        }
    }
}

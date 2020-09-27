package com.adev.market.websocket.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MessageHandlerStorage {
    private static final Logger LOG = LoggerFactory.getLogger(MessageHandlerStorage.class);

    private final ConcurrentHashMap<String,WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<String>> channels = new ConcurrentHashMap<>();

    /**
     * Get all webSocket sessions.
     *
     * @return sessions
     */
    public ConcurrentHashMap<String,WebSocketSession> getSessions() {
        return sessions;
    }

    public WebSocketSession getSession(String sessionId){
        return sessions.get(sessionId);
    }


    public Set<String> getSessionIdSet(String channel){
        return channels.get(channel);
    }

    /**
     * Add webSocket session.
     *
     * @param session session
     */
    public void addSession(WebSocketSession session) {
        sessions.put(session.getId(),session);
        LOG.debug("Add new session [{}], current sessions size is {}.", session.getId(), sessions.size());
    }

    /**
     * Remove webSocket session.
     *
     * @param session session
     */
    public void removeSession(WebSocketSession session) {
        sessions.remove(session.getId());
        LOG.debug("Remove session [{}], current sessions size is {}.", session.getId(), sessions.size());
    }

    /**
     * Subscribe message channel.
     *
     * @param channel channel
     * @param session session
     */
    public void addChannel(String channel, WebSocketSession session) {
        Set<String> sessionIdSet = channels.get(channel);
        if(null==sessionIdSet){
            sessionIdSet=ConcurrentHashMap.newKeySet();
            channels.put(channel,sessionIdSet);
        }
        sessionIdSet.add(session.getId());
        LOG.debug("Subscribe channel [{}], the session id is {}.", channel, session.getId());
    }

    /**
     * Unsubscribe message channel.
     *
     * @param channel channel
     * @param session session
     */
    public void removeChannel(String channel, WebSocketSession session) {
        Set<String> sessionIdSet = channels.get(channel);
        if (sessionIdSet == null) return;
        sessionIdSet.remove(session.getId());
        LOG.debug("Unsubscribe channel [{}], the session id is {}.", channel, session.getId());
    }
}

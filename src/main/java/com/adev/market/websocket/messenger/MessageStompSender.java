package com.adev.market.websocket.messenger;

public interface MessageStompSender {
    /**
     * Sendmessage to all.
     *
     * @param message JSONString (must include 'channel' property)
     */
    void sendMessage(String message);

    /**
     * Send message to all.
     *
     * @param channel channel
     * @param payload payload
     */
    void sendMessage(String channel, Object payload);
}

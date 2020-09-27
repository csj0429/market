package com.adev.market.websocket.messenger;

public interface MessageSender {
    /**
     * 发送消息
     * @param channel
     * @param message
     */
    void sendMessage(String channel, String message);
}

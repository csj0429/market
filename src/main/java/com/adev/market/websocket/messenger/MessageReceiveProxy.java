package com.adev.market.websocket.messenger;

public interface MessageReceiveProxy {
    /**
     * Receive message.
     *
     * @param message message
     */
    void receiveMessage(String message);
}

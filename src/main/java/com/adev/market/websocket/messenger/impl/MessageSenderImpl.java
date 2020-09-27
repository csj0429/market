package com.adev.market.websocket.messenger.impl;

import com.adev.market.websocket.handler.MessageHandler;
import com.adev.market.websocket.messenger.MessageSender;
import com.adev.market.websocket.messenger.MessageStompSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageSenderImpl implements MessageSender {

    @Autowired
    private MessageStompSender messageStompSender;

    @Override
    public void sendMessage(String channel, String message) {
        messageStompSender.sendMessage(message);
        MessageHandler.groupSendMessage(channel,message);
    }
}

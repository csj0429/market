package com.adev.market.websocket.messenger.impl;

import com.adev.market.websocket.messenger.MessageReceiveProxy;
import com.adev.market.websocket.messenger.MessageSender;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageReceiveProxyImpl implements MessageReceiveProxy {

    private static final Logger LOG = LoggerFactory.getLogger(MessageReceiveProxyImpl.class);

    @Autowired
    private MessageSender messageSender;

    @Override
    public void receiveMessage(String message) {
        LOG.info("receiveMessage message :{}",message);
        String channel=getChannel(message);
        if(StringUtils.isNotBlank(channel)) {
            messageSender.sendMessage(channel, message);
        }
    }

    private String getChannel(String message){
        JSONObject messageJson=JSON.parseObject(message);
        if(null!=messageJson) {
            String channel = messageJson.getString("channel");
            return channel;
        }
        return null;
    }

}

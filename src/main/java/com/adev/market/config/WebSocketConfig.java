package com.adev.market.config;

import com.adev.market.websocket.handler.MessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketConfigurer, WebSocketMessageBrokerConfigurer {
    /**
     * On the Servlet stack the Spring Framework provides both server (and
     * also client) support for the SockJS protocol.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/sockjs").setAllowedOrigins("*").withSockJS();
    }

    /**
     * Mapping the WebSocket handler to a specific URL, Customize the
     * initial HTTP WebSocket handshake request through a
     * HandshakeInterceptor.
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(messageHandler(), "/websocket")
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler messageHandler() {
        return new MessageHandler();
    }
}

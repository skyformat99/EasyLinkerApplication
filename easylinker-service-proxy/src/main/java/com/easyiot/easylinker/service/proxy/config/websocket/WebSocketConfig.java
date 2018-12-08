package com.easyiot.easylinker.service.proxy.config.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 开启WebSocket支持
 */
@EnableWebSocket
@Configuration
public class WebSocketConfig implements WebSocketConfigurer {
    @Value("${easylinker.websocket.server.host}")
    String webSocketHost;
    @Value("${easylinker.websocket.server.path}")
    String webSocketPath;

    private final JmsTemplate jmsTemplate;
    @Autowired
    public WebSocketConfig(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }


    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(new WebSocketHandler(jmsTemplate),
                webSocketPath).setAllowedOrigins(webSocketHost);
    }


}

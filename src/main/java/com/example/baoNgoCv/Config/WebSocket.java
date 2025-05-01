package com.example.baoNgoCv.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocket implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("\"http://localhost:7081\"")  // Cho phép tất cả các nguồn kết nối
                .withSockJS();  // Hỗ trợ SockJS fallback nếu WebSocket không khả dụng
    }




    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");  // Kênh gửi dữ liệu đến client
        registry.setApplicationDestinationPrefixes("/app");  // Prefix cho các message từ client đến server
    }


}

//package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//@Configuration
//@EnableWebSocketMessageBroker
//@RequiredArgsConstructor
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.enableSimpleBroker("/topic", "/queue")
//                .setHeartbeatValue(new long[]{10000, 10000}); // 10s ping-pong
//        registry.setApplicationDestinationPrefixes("/app");
//        registry.setUserDestinationPrefix("/user");
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws")
//                .setAllowedOriginPatterns(
//                        "http://localhost:5173",
//                        "http://localhost:3000"
//                )
//                .withSockJS()
//                .setSessionCookieNeeded(false); // dùng JWT, tránh cookie
//    }
//
//}

package com.spring.martadmin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 서버에 접속할 EndPoint를 설정한다. 엔드포인트는 여러개 추가 가능하다.
        // Client에서 Websocket대신 향상된 SockJS로 접속하려면 .withSockJS()를 붙여준다.

        registry.addEndpoint("/ws-stomp").setAllowedOrigins("*")
                .withSockJS(); // sock.js를 통하여 낮은 버전의 브라우저에서도 websocket이 동작할 수 있게 함
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub"); // 메세지 브로커가 /sub이 들어가는 구독자들에게 메세지를 전송한다.
        registry.setApplicationDestinationPrefixes("/pub"); // 클라이언트가 서버에게 /pub을 붙이고 메세지를 전달한 주소

    }
}

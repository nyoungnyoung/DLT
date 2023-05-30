package com.dopamines.backend.webSocket.config;

import com.dopamines.backend.webSocket.handler.PositionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Slf4j
@Configuration
@RequiredArgsConstructor
// WebSocket을 활성화
@EnableWebSocket
// 핸들러를 이용해 WebSocket을 활성화하기 위한 Config
public class WebSocketConfig implements WebSocketConfigurer {

    private final PositionHandler positionHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        // endpoint 설정 : /ws/position
        // 이를 통해서 ws://localhost:8081/ws/position 으로 요청이 들어오면 websocket 통신을 진행합니다.
        registry.addHandler(positionHandler, "ws/position").setAllowedOrigins("*");

        // WebSocket에 접속하기 위한 Endpoint는 /position 으로 설정
        // Endpoint란 API가 서버에서 자원(resource)에 접근할 수 있도록 하는 URL
        // ws://localhost:8081/ws/position으로 커넥션을 연결하고 메세지 통신을 할 수 있는 준비
    }
}
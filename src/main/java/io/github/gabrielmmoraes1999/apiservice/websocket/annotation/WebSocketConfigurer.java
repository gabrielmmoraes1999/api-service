package io.github.gabrielmmoraes1999.apiservice.websocket.annotation;

public interface WebSocketConfigurer {
    void registerWebSocketHandlers(WebSocketHandlerRegistry registry);
}

package io.github.gabrielmmoraes1999.apiservice.websocket.annotation;

import io.github.gabrielmmoraes1999.apiservice.websocket.WebSocketHandler;

public interface WebSocketHandlerRegistry {
    WebSocketHandlerRegistration addHandler(WebSocketHandler webSocketHandler, String path);
}

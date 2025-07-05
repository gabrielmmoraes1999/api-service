package io.github.gabrielmmoraes1999.apiservice.websocket.annotation;

import io.github.gabrielmmoraes1999.apiservice.websocket.WebSocketHandler;

public interface WebSocketHandlerRegistration {
    WebSocketHandlerRegistration addHandler(WebSocketHandler handler, String path);

    //WebSocketHandlerRegistration setHandshakeHandler(HandshakeHandler handshakeHandler);

    //WebSocketHandlerRegistration addInterceptors(HandshakeInterceptor... interceptors);

    WebSocketHandlerRegistration setAllowedOrigins(String... origins);

    WebSocketHandlerRegistration setAllowedOriginPatterns(String... originPatterns);

    //SockJsServiceRegistration withSockJS();
}
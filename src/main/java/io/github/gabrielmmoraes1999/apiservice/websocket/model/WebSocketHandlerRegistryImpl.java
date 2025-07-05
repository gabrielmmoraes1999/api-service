package io.github.gabrielmmoraes1999.apiservice.websocket.model;

import io.github.gabrielmmoraes1999.apiservice.websocket.WebSocketHandler;
import io.github.gabrielmmoraes1999.apiservice.websocket.annotation.WebSocketHandlerRegistration;
import io.github.gabrielmmoraes1999.apiservice.websocket.annotation.WebSocketHandlerRegistry;

public class WebSocketHandlerRegistryImpl implements WebSocketHandlerRegistry {

    private final WebSocketHandlerRegistrationImpl registration;

    public WebSocketHandlerRegistryImpl(WebSocketHandlerRegistrationImpl registration) {
        this.registration = registration;
    }

    @Override
    public WebSocketHandlerRegistration addHandler(WebSocketHandler webSocketHandler, String paths) {
        return registration.addHandler(webSocketHandler, paths);
    }

}

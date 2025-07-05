package io.github.gabrielmmoraes1999.apiservice.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

@WebSocket
public abstract class WebSocketHandler {

    @OnWebSocketConnect
    public void afterConnectionEstablished(Session session) throws Exception {

    }

    @OnWebSocketMessage
    public void handleMessage(Session session, String message) throws Exception {

    }

    @OnWebSocketError
    public void handleTransportError(Session session, Throwable exception) throws Exception {

    }

    @OnWebSocketClose
    public void afterConnectionClosed(Session session, int statusCode, String reason) throws Exception {

    }

}

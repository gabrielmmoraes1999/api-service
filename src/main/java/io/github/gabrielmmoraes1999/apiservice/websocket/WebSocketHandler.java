package io.github.gabrielmmoraes1999.apiservice.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

@WebSocket
public abstract class WebSocketHandler {

    @OnWebSocketConnect
    public void afterConnectionEstablished(Session session) throws Exception {
        afterConnectionEstablished(new WebSocketSession(session));
    }

    @OnWebSocketMessage
    public void handleMessage(Session session, String message) throws Exception {
        handleMessage(new WebSocketSession(session), message);
    }

    @OnWebSocketError
    public void handleTransportError(Session session, Throwable exception) throws Exception {
        handleTransportError(new WebSocketSession(session), exception);
    }

    @OnWebSocketClose
    public void afterConnectionClosed(Session session, int statusCode, String reason) throws Exception {
        afterConnectionClosed(new WebSocketSession(session), statusCode, reason);
    }

    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

    }

    public void handleMessage(WebSocketSession session, String message) throws Exception {

    }

    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    public void afterConnectionClosed(WebSocketSession session, int statusCode, String reason) throws Exception {

    }

}

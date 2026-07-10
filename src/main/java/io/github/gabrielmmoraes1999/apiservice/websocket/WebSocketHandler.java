package io.github.gabrielmmoraes1999.apiservice.websocket;

import org.eclipse.jetty.websocket.api.Session;

public abstract class WebSocketHandler implements Session.Listener.AutoDemanding {

    private WebSocketSession wsSession;

    @Override
    public void onWebSocketOpen(Session session) {
        wsSession = new WebSocketSession(session);
        try {
            afterConnectionEstablished(wsSession);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void onWebSocketText(String message) {
        try {
            handleMessage(wsSession, message);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        try {
            handleTransportError(wsSession, cause);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason, org.eclipse.jetty.websocket.api.Callback callback) {
        try {
            afterConnectionClosed(wsSession, statusCode, reason);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            callback.succeed();
        }
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

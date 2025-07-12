package io.github.gabrielmmoraes1999.service.websocket.config;

import io.github.gabrielmmoraes1999.apiservice.websocket.WebSocketHandler;
import io.github.gabrielmmoraes1999.apiservice.websocket.WebSocketSession;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class WebSocketHandlerConfig extends WebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String authHeader = session.getUpgradeRequest().getHeader("Authorization");
        System.out.println("Cliente conectado: " + session.getRemoteAddress());
        session.getRemote().sendString("Bem-vindo ao WebSocket!");
    }

    @Override
    public void handleMessage(WebSocketSession session, String message) throws Exception {
        System.out.println("Recebido: " + message);
        session.getRemote().sendString("Broadcast: " + message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("Erro na conexão: " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, int statusCode, String reason) throws Exception {
        System.out.println("Conexão fechada: " + session.getRemoteAddress());
    }

}

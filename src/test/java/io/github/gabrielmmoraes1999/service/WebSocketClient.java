package io.github.gabrielmmoraes1999.service;

import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class WebSocketClient {

    public static void main(String[] args) {
        String destUri = "wss://localhost/websocket"; // Ajuste porta se necessário
        String token = "token"; // Substitua pelo token real

        SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
        sslContextFactory.setTrustAll(true); // Confia em qualquer certificado
        sslContextFactory.setEndpointIdentificationAlgorithm(null); // Desativa verificação do hostname

        org.eclipse.jetty.websocket.client.WebSocketClient client = new org.eclipse.jetty.websocket.client.WebSocketClient(sslContextFactory);
        ChatSocket socket = new ChatSocket();

        try {
            client.start();

            URI echoUri = new URI(destUri);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            request.setHeader("Authorization", "Bearer " + token);

            System.out.println("Conectando a : " + echoUri);
            client.connect(socket, echoUri, request);

            // Aguarda até a conexão ser fechada ou timeout
            socket.awaitClose(10, TimeUnit.SECONDS);

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                client.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class ChatSocket extends WebSocketAdapter {
        private final CountDownLatch closeLatch = new CountDownLatch(1);

        public void awaitClose(int duration, TimeUnit unit) throws InterruptedException {
            closeLatch.await(duration, unit);
        }

        @Override
        public void onWebSocketConnect(Session sess) {
            super.onWebSocketConnect(sess);
            System.out.println("Conectado!");
            try {
                sess.getRemote().sendString("Olá do cliente!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onWebSocketText(String message) {
            System.out.println("Recebido do servidor: " + message);
        }

        @Override
        public void onWebSocketClose(int statusCode, String reason) {
            System.out.println("Conexão fechada: [" + statusCode + "] " + reason);
            closeLatch.countDown();
        }

        @Override
        public void onWebSocketError(Throwable cause) {
            System.out.println("Erro no WebSocket:");
            cause.printStackTrace();
            closeLatch.countDown();
        }
    }
}

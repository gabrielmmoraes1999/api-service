package io.github.gabrielmmoraes1999.service;

import io.github.gabrielmmoraes1999.db.Repository;
import io.github.gabrielmmoraes1999.service.config.JwtConfig;
import io.github.gabrielmmoraes1999.service.entity.User;
import io.github.gabrielmmoraes1999.service.repository.UserRepository;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Cliente manual para testar o WebSocket do servidor de demonstração.
 *
 * Uso:
 * 1. Suba o servidor: mvn exec:java -Dexec.mainClass=io.github.gabrielmmoraes1999.service.Main -Dexec.classpathScope=test
 * 2. Execute o cliente: mvn exec:java -Dexec.mainClass=io.github.gabrielmmoraes1999.service.WebSocketClient -Dexec.classpathScope=test -Dexec.args="wss://localhost:443/websocket SEU_USER_UUID"
 */
public class WebSocketClient {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            printUsage();
            return;
        }

        String destUri = args[0];
        String userUuid = args.length > 1 ? args[1] : null;

        if (userUuid == null) {
            System.err.println("Informe o UUID do usuário ADMIN cadastrado no banco.");
            printUsage();
            return;
        }

        UserRepository userRepository = Repository.createRepository(UserRepository.class);
        User user = userRepository.findById(userUuid);
        if (user == null) {
            System.err.println("Usuário não encontrado para o UUID: " + userUuid);
            return;
        }

        JwtConfig jwtConfig = new JwtConfig();
        String token = jwtConfig.generateToken(user.getUuid(), 3600);

        SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
        sslContextFactory.setTrustAll(true);
        sslContextFactory.setEndpointIdentificationAlgorithm(null);

        HttpClient httpClient = new HttpClient();
        httpClient.setSslContextFactory(sslContextFactory);

        org.eclipse.jetty.websocket.client.WebSocketClient client = new org.eclipse.jetty.websocket.client.WebSocketClient(httpClient);
        ChatSocket socket = new ChatSocket();

        try {
            client.start();

            URI echoUri = new URI(destUri);
            ClientUpgradeRequest request = new ClientUpgradeRequest(echoUri);
            request.setHeader("Authorization", "Bearer " + token);

            System.out.println("Conectando a: " + echoUri + " (usuário: " + user.getUsername() + ")");
            CompletableFuture<Session> sessionFuture = client.connect(socket, request, null);
            sessionFuture.orTimeout(10, TimeUnit.SECONDS).join();

            socket.awaitClose(30, TimeUnit.SECONDS);

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            client.stop();
        }
    }

    private static void printUsage() {
        System.out.println("Uso: WebSocketClient <wss-url> <user-uuid>");
        System.out.println("Exemplo: wss://localhost:443/websocket 3fa85f64-5717-4562-b3fc-2c963f66afa6");
    }

    public static class ChatSocket implements Session.Listener.AutoDemanding {

        private final CountDownLatch closeLatch = new CountDownLatch(1);

        public void awaitClose(int duration, TimeUnit unit) throws InterruptedException {
            closeLatch.await(duration, unit);
        }

        @Override
        public void onWebSocketOpen(Session session) {
            System.out.println("Conectado!");
            session.sendText("Olá do cliente!", Callback.NOOP);
        }

        @Override
        public void onWebSocketText(String message) {
            System.out.println("Recebido do servidor: " + message);
        }

        @Override
        public void onWebSocketClose(int statusCode, String reason, Callback callback) {
            System.out.println("Conexão fechada: [" + statusCode + "] " + reason);
            closeLatch.countDown();
            callback.succeed();
        }

        @Override
        public void onWebSocketError(Throwable cause) {
            System.out.println("Erro no WebSocket:");
            cause.printStackTrace();
            closeLatch.countDown();
        }
    }
}

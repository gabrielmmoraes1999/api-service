package io.github.gabrielmmoraes1999.apiservice.websocket;

import org.eclipse.jetty.ee10.websocket.server.JettyServerUpgradeRequest;
import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class WebSocketSession {

    private final Session delegate;

    public WebSocketSession(Session delegate) {
        this.delegate = delegate;
    }

    public void close() {
        delegate.close(StatusCode.NORMAL, null, Callback.NOOP);
    }

    public void close(int statusCode, String reason) {
        delegate.close(statusCode, reason, Callback.NOOP);
    }

    public void disconnect() {
        delegate.disconnect();
    }

    public long getIdleTimeout() {
        Duration idleTimeout = delegate.getIdleTimeout();
        return idleTimeout != null ? idleTimeout.toMillis() : 0;
    }

    public InetSocketAddress getLocalAddress() {
        return toInetSocketAddress(delegate.getLocalSocketAddress());
    }

    public String getProtocolVersion() {
        return delegate.getProtocolVersion();
    }

    public RemoteEndpoint getRemote() {
        return new RemoteEndpoint(delegate);
    }

    public InetSocketAddress getRemoteAddress() {
        return toInetSocketAddress(delegate.getRemoteSocketAddress());
    }

    public UpgradeRequest getUpgradeRequest() {
        return delegate.getUpgradeRequest();
    }

    public UpgradeResponse getUpgradeResponse() {
        return delegate.getUpgradeResponse();
    }

    public boolean isOpen() {
        return delegate.isOpen();
    }

    public boolean isSecure() {
        return delegate.isSecure();
    }

    public void setIdleTimeout(long timeout) {
        delegate.setIdleTimeout(Duration.ofMillis(timeout));
    }

    public Object getAttribute(String name) {
        UpgradeRequest upgradeRequest = delegate.getUpgradeRequest();
        if (upgradeRequest instanceof JettyServerUpgradeRequest jettyRequest) {
            return jettyRequest.getHttpServletRequest().getAttribute(name);
        }
        return null;
    }

    public void sendMessage(String message) throws IOException {
        getRemote().sendString(message);
    }

    private static InetSocketAddress toInetSocketAddress(SocketAddress socketAddress) {
        if (socketAddress instanceof InetSocketAddress inetSocketAddress) {
            return inetSocketAddress;
        }
        return null;
    }

    public static class RemoteEndpoint {

        private final Session session;

        public RemoteEndpoint(Session session) {
            this.session = session;
        }

        public void sendString(String message) throws IOException {
            CompletableFuture<Void> future = new CompletableFuture<>();
            session.sendText(message, Callback.from(() -> future.complete(null), future::completeExceptionally));
            try {
                future.get();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IOException(ex);
            } catch (ExecutionException ex) {
                throw new IOException(ex.getCause());
            }
        }
    }

}

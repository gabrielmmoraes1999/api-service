package io.github.gabrielmmoraes1999.apiservice.websocket;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetSocketAddress;

public class WebSocketSession implements Session {

    private final Session delegate;

    public WebSocketSession(Session delegate) {
        this.delegate = delegate;
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public void close(CloseStatus closeStatus) {
        delegate.close(closeStatus);
    }

    @Override
    public void close(int i, String s) {
        delegate.close(i, s);
    }

    @Override
    public void disconnect() throws IOException {
        delegate.disconnect();
    }

    @Override
    public long getIdleTimeout() {
        return delegate.getIdleTimeout();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return delegate.getLocalAddress();
    }

    @Override
    public WebSocketPolicy getPolicy() {
        return delegate.getPolicy();
    }

    @Override
    public String getProtocolVersion() {
        return delegate.getProtocolVersion();
    }

    @Override
    public RemoteEndpoint getRemote() {
        return delegate.getRemote();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return delegate.getRemoteAddress();
    }

    @Override
    public UpgradeRequest getUpgradeRequest() {
        return delegate.getUpgradeRequest();
    }

    @Override
    public UpgradeResponse getUpgradeResponse() {
        return delegate.getUpgradeResponse();
    }

    @Override
    public boolean isOpen() {
        return delegate.isOpen();
    }

    @Override
    public boolean isSecure() {
        return delegate.isSecure();
    }

    @Override
    public void setIdleTimeout(long l) {
        delegate.setIdleTimeout(l);
    }

    @Override
    public SuspendToken suspend() {
        return delegate.suspend();
    }

    public Object getAttribute(String s) {
        ServletUpgradeRequest upgradeRequest = (ServletUpgradeRequest) delegate.getUpgradeRequest();
        HttpServletRequest httpRequest = upgradeRequest.getHttpServletRequest();
        return httpRequest.getAttribute(s);
    }

}

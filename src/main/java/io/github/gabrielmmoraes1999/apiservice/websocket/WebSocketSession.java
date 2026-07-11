package io.github.gabrielmmoraes1999.apiservice.websocket;

import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.api.exceptions.WebSocketTimeoutException;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class WebSocketSession implements Session {

    private final Session delegate;
    private final Map<String, Object> attributes;

    public WebSocketSession(Session delegate) {
        this(delegate, new HashMap<>());
    }

    public WebSocketSession(Session delegate, Map<String, Object> attributes) {
        this.delegate = delegate;
        this.attributes = attributes;
    }

    @Override
    public void demand() {
        delegate.demand();
    }

    @Override
    public void sendBinary(ByteBuffer buffer, Callback callback) {
        delegate.sendBinary(buffer, callback);
    }

    @Override
    public void sendPartialBinary(ByteBuffer buffer, boolean last, Callback callback) {
        delegate.sendPartialBinary(buffer, last, callback);
    }

    @Override
    public void sendText(String text, Callback callback) {
        delegate.sendText(text, callback);
    }

    @Override
    public void sendPartialText(String text, boolean last, Callback callback) {
        delegate.sendPartialText(text, last, callback);
    }

    @Override
    public void sendPing(ByteBuffer applicationData, Callback callback) {
        delegate.sendPing(applicationData, callback);
    }

    @Override
    public void sendPong(ByteBuffer applicationData, Callback callback) {
        delegate.sendPong(applicationData, callback);
    }

    @Override
    public void close(int statusCode, String reason, Callback callback) {
        delegate.close(statusCode, reason, callback);
    }

    @Override
    public void disconnect() {
        delegate.disconnect();
    }

    @Override
    public SocketAddress getLocalSocketAddress() {
        return delegate.getLocalSocketAddress();
    }

    @Override
    public SocketAddress getRemoteSocketAddress() {
        return delegate.getRemoteSocketAddress();
    }

    @Override
    public String getProtocolVersion() {
        return delegate.getProtocolVersion();
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
    public void addIdleTimeoutListener(Predicate<WebSocketTimeoutException> onIdleTimeout) {
        delegate.addIdleTimeoutListener(onIdleTimeout);
    }

    @Override
    public Duration getIdleTimeout() {
        return delegate.getIdleTimeout();
    }

    @Override
    public void setIdleTimeout(Duration duration) {
        delegate.setIdleTimeout(duration);
    }

    @Override
    public int getInputBufferSize() {
        return delegate.getInputBufferSize();
    }

    @Override
    public void setInputBufferSize(int size) {
        delegate.setInputBufferSize(size);
    }

    @Override
    public int getOutputBufferSize() {
        return delegate.getOutputBufferSize();
    }

    @Override
    public void setOutputBufferSize(int size) {
        delegate.setOutputBufferSize(size);
    }

    @Override
    public long getMaxBinaryMessageSize() {
        return delegate.getMaxBinaryMessageSize();
    }

    @Override
    public void setMaxBinaryMessageSize(long size) {
        delegate.setMaxBinaryMessageSize(size);
    }

    @Override
    public long getMaxTextMessageSize() {
        return delegate.getMaxTextMessageSize();
    }

    @Override
    public void setMaxTextMessageSize(long size) {
        delegate.setMaxTextMessageSize(size);
    }

    @Override
    public long getMaxFrameSize() {
        return delegate.getMaxFrameSize();
    }

    @Override
    public void setMaxFrameSize(long maxFrameSize) {
        delegate.setMaxFrameSize(maxFrameSize);
    }

    @Override
    public boolean isAutoFragment() {
        return delegate.isAutoFragment();
    }

    @Override
    public void setAutoFragment(boolean autoFragment) {
        delegate.setAutoFragment(autoFragment);
    }

    @Override
    public int getMaxOutgoingFrames() {
        return delegate.getMaxOutgoingFrames();
    }

    @Override
    public void setMaxOutgoingFrames(int maxOutgoingFrames) {
        delegate.setMaxOutgoingFrames(maxOutgoingFrames);
    }

    public Object getAttribute(String s) {
        return attributes.get(s);
    }

    public void sendMessage(String message) {
        delegate.sendText(message, Callback.NOOP);
    }

}

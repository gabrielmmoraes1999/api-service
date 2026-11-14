package io.github.gabrielmmoraes1999.apiservice.websocket;

import io.github.gabrielmmoraes1999.apiservice.websocket.annotation.HandshakeInterceptor;
import io.github.gabrielmmoraes1999.apiservice.websocket.model.WebSocketServletImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;

class WebSocketHandshakeRejectTest {

    @Test
    void rejectedHandshakeDoesNotOpenTheSocket() throws Exception {
        AtomicBoolean attributeCopied = new AtomicBoolean();
        HandshakeInterceptor reject = new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    Map<String, Object> attributes) {
                return false;
            }

            @Override
            public void afterHandshake(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    Exception ex) {
            }
        };

        WebSocketServletImpl servlet = new WebSocketServletImpl(new WebSocketHandler() {
        }, List.of(reject));

        HttpServletRequest request = (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "setAttribute" -> {
                        attributeCopied.set(true);
                        yield null;
                    }
                    case "toString" -> "request";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> null;
                });
        HttpServletResponse response = (HttpServletResponse) Proxy.newProxyInstance(
                HttpServletResponse.class.getClassLoader(),
                new Class<?>[]{HttpServletResponse.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "toString" -> "response";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> null;
                });

        servlet.service(request, response);

        assertFalse(attributeCopied.get());
    }

}

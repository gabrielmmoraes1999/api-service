package io.github.gabrielmmoraes1999.apiservice.websocket.model;

import io.github.gabrielmmoraes1999.apiservice.websocket.WebSocketHandler;
import io.github.gabrielmmoraes1999.apiservice.websocket.annotation.HandshakeInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.ee10.websocket.server.JettyWebSocketServlet;
import org.eclipse.jetty.ee10.websocket.server.JettyWebSocketServletFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebSocketServletImpl extends JettyWebSocketServlet {

    private final Class<? extends WebSocketHandler> handlerClass;
    private final List<HandshakeInterceptor> handshakeInterceptorList;

    public WebSocketServletImpl(WebSocketHandler handler, List<HandshakeInterceptor> handshakeInterceptorList) {
        this.handlerClass = handler.getClass();
        this.handshakeInterceptorList = handshakeInterceptorList;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        Map<String, Object> attributes = new HashMap<>();
        boolean byPass = true;

        try {
            for (HandshakeInterceptor handshakeInterceptor : handshakeInterceptorList) {
                byPass = handshakeInterceptor.beforeHandshake(req, resp, attributes);

                if (!byPass) {
                    break;
                }
            }

            if (byPass) {
                attributes.forEach(req::setAttribute);
                super.service(req, resp);
            }

            for (HandshakeInterceptor handshakeInterceptor : handshakeInterceptorList) {
                handshakeInterceptor.afterHandshake(req, resp, null);
            }
        } catch (Exception ex) {
            for (HandshakeInterceptor handshakeInterceptor : handshakeInterceptorList) {
                handshakeInterceptor.afterHandshake(req, resp, ex);
            }
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void configure(JettyWebSocketServletFactory factory) {
        factory.addMapping("/", (req, res) -> {
            try {
                return handlerClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

}

package io.github.gabrielmmoraes1999.apiservice.websocket.model;

import io.github.gabrielmmoraes1999.apiservice.websocket.WebSocketHandler;
import io.github.gabrielmmoraes1999.apiservice.websocket.annotation.HandshakeInterceptor;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebSocketServletImpl extends WebSocketServlet {

    private final WebSocketHandler handler;
    private final List<HandshakeInterceptor> handshakeInterceptorList;

    public WebSocketServletImpl(WebSocketHandler handler, List<HandshakeInterceptor> handshakeInterceptorList) {
        this.handler = handler;
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
    public void configure(WebSocketServletFactory factory) {
        factory.register(handler.getClass());
    }

}

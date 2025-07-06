package io.github.gabrielmmoraes1999.apiservice.websocket.model;

import io.github.gabrielmmoraes1999.apiservice.websocket.WebSocketHandler;
import io.github.gabrielmmoraes1999.apiservice.websocket.annotation.HandshakeInterceptor;
import io.github.gabrielmmoraes1999.apiservice.websocket.annotation.WebSocketHandlerRegistration;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.ArrayList;
import java.util.List;

public class WebSocketHandlerRegistrationImpl implements WebSocketHandlerRegistration {

    private final ServletContextHandler servletContextHandler;
    private final List<HandshakeInterceptor> handshakeInterceptorList;

    public WebSocketHandlerRegistrationImpl(ServletContextHandler servletContextHandler) {
        this.servletContextHandler = servletContextHandler;
        this.handshakeInterceptorList = new ArrayList<>();
    }

    @Override
    public WebSocketHandlerRegistration addHandler(WebSocketHandler handler, String path) {
        servletContextHandler.addServlet(
                new ServletHolder(
                        path.replace("/", ""),
                        new WebSocketServletImpl(handler, handshakeInterceptorList)
                ),
                path
        );

        return this;
    }

    @Override
    public WebSocketHandlerRegistration addInterceptor(HandshakeInterceptor interceptor) {
        this.handshakeInterceptorList.add(interceptor);
        return this;
    }

}

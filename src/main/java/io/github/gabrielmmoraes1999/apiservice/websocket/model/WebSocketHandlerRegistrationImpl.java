package io.github.gabrielmmoraes1999.apiservice.websocket.model;

import io.github.gabrielmmoraes1999.apiservice.websocket.WebSocketHandler;
import io.github.gabrielmmoraes1999.apiservice.websocket.annotation.WebSocketHandlerRegistration;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class WebSocketHandlerRegistrationImpl implements WebSocketHandlerRegistration {

    private final ServletContextHandler servletContextHandler;

    public WebSocketHandlerRegistrationImpl(ServletContextHandler servletContextHandler) {
        this.servletContextHandler = servletContextHandler;
    }

    @Override
    public WebSocketHandlerRegistration addHandler(WebSocketHandler handler, String path) {
        ServletHolder wsHolder = new ServletHolder(path.replace("/", ""), new WebSocketServletImpl(handler));
        servletContextHandler.addServlet(wsHolder, path);
        return null;
    }

    @Override
    public WebSocketHandlerRegistration setAllowedOrigins(String... origins) {
        return null;
    }

    @Override
    public WebSocketHandlerRegistration setAllowedOriginPatterns(String... originPatterns) {
        return null;
    }

}

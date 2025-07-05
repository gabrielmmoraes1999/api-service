package io.github.gabrielmmoraes1999.apiservice.websocket.model;

import io.github.gabrielmmoraes1999.apiservice.websocket.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class WebSocketServletImpl extends WebSocketServlet {

    private final WebSocketHandler handler;

    public WebSocketServletImpl(WebSocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(handler.getClass());
    }

}

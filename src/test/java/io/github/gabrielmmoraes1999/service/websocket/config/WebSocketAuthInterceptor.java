package io.github.gabrielmmoraes1999.service.websocket.config;

import io.github.gabrielmmoraes1999.apiservice.websocket.annotation.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(
            HttpServletRequest request,
            HttpServletResponse response,
            Map<String, Object> attributes) throws Exception {
        return true;
    }

    @Override
    public void afterHandshake(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception ex) {

    }

}

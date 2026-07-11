package io.github.gabrielmmoraes1999.apiservice.websocket.annotation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

public interface HandshakeInterceptor {
    boolean beforeHandshake(HttpServletRequest request, HttpServletResponse response, Map<String, Object> attributes) throws Exception;
    void afterHandshake(HttpServletRequest request, HttpServletResponse response, Exception ex);
}

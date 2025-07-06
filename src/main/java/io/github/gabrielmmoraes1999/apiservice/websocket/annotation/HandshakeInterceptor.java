package io.github.gabrielmmoraes1999.apiservice.websocket.annotation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface HandshakeInterceptor {
    boolean beforeHandshake(HttpServletRequest request, HttpServletResponse response, Map<String, Object> attributes) throws Exception;
    void afterHandshake(HttpServletRequest request, HttpServletResponse response, Exception ex);
}

package io.github.gabrielmmoraes1999.apiservice.auth;

import io.github.gabrielmmoraes1999.apiservice.security.SecurityContextHolder;
import io.github.gabrielmmoraes1999.apiservice.security.crypto.CryptoUtils;
import io.github.gabrielmmoraes1999.apiservice.utils.Message;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BasicAuthFilter implements Filter {

    private final String username;
    private final String password;

    public BasicAuthFilter(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;
            String authHeader = req.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Basic ")) {
                unauthorized(resp);
                return;
            }

            String base64Credentials = authHeader.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
            String[] values = credentials.split(":", 2);

            if (values.length != 2
                    || !CryptoUtils.constantTimeEquals(username, values[0])
                    || !CryptoUtils.constantTimeEquals(password, values[1])) {
                unauthorized(resp);
                return;
            }

            chain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private void unauthorized(HttpServletResponse resp) throws IOException {
        resp.setHeader("WWW-Authenticate", "Basic realm=\"API\"");
        Message.error(resp, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}

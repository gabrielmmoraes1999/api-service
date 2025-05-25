package io.github.gabrielmmoraes1999.apiservice.auth;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            unauthorized(resp);
            return;
        }

        String base64Credentials = authHeader.substring("Basic ".length());
        String credentials = new String(Base64.getDecoder().decode(base64Credentials));
        String[] values = credentials.split(":", 2);

        if (values.length != 2 || !username.equals(values[0]) || !password.equals(values[1])) {
            unauthorized(resp);
            return;
        }

        chain.doFilter(request, response);
    }

    private void unauthorized(HttpServletResponse resp) throws IOException {
        resp.setHeader("WWW-Authenticate", "Basic realm=\"API\"");
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        resp.getWriter().write("401 - Unauthorized");
    }
}

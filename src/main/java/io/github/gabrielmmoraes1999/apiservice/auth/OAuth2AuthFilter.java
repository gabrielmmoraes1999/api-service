package io.github.gabrielmmoraes1999.apiservice.auth;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Deprecated
public class OAuth2AuthFilter implements Filter {

    private final OAuth2TokenValidator validator;

    public OAuth2AuthFilter(OAuth2TokenValidator validator) {
        this.validator = validator;
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer ".length());
            if (validator.validate(token)) {
                chain.doFilter(req, res); // Token válido
                return;
            }
        }

        ((HttpServletResponse) res).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
    }
}

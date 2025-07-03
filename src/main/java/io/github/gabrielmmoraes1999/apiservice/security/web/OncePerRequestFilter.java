package io.github.gabrielmmoraes1999.apiservice.security.web;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class OncePerRequestFilter implements Filter {

    private boolean alreadyFiltered(HttpServletRequest request) {
        return request.getAttribute(this.getClass().getName()) != null;
    }

    @Override
    public final void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        if (alreadyFiltered(request)) {
            chain.doFilter(req, res);
            return;
        }
        request.setAttribute(this.getClass().getName(), Boolean.TRUE);
        doFilterInternal(request, (HttpServletResponse) res, chain);
    }

    protected abstract void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException;
}

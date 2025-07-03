package io.github.gabrielmmoraes1999.apiservice.auth;

import io.github.gabrielmmoraes1999.apiservice.context.ApplicationContext;
import io.github.gabrielmmoraes1999.apiservice.security.*;
import io.github.gabrielmmoraes1999.apiservice.security.jwt.ProviderJwt;
import io.github.gabrielmmoraes1999.apiservice.security.web.SecurityFilterChain;
import io.github.gabrielmmoraes1999.apiservice.utils.Message;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JwtAuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getRequestURI() + "/";
        String method = request.getMethod();

        if (Objects.equals("/oauth2/token/", path) && Objects.equals("POST", method)) {
            chain.doFilter(req, res);
            return;
        }

        if (SecurityFilterChain.getRules() == null) {
            chain.doFilter(req, res);
            return;
        }

        if (SecurityFilterChain.getRule(path, method, SecurityRule.AccessType.PERMIT_ALL).isPresent()) {
            chain.doFilter(req, res);
            return;
        }

        List<SecurityRule> ruleList = SecurityFilterChain.getListRule(path, method, SecurityRule.AccessType.HAS_ROLE);

        if (ruleList.isEmpty()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado");
            return;
        }

        List<String> accessTypeList = ruleList.stream()
                .flatMap(rule-> rule.getRoles().stream())
                .collect(Collectors.toList());

        String token = recoverToken(request);
        if (token == null) {
            //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token away");
            Message.error(response, HttpServletResponse.SC_UNAUTHORIZED, "Token away");
            return;
        }

        String subject = ApplicationContext.getBean(ProviderJwt.class, new ProviderJwt()).checkToken(token);
        if (subject == null) {
            Message.error(response, HttpServletResponse.SC_UNAUTHORIZED, "Token invalid");
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            Message.error(response, HttpServletResponse.SC_UNAUTHORIZED, "User not found");
            return;
        }

        UserDetails userDetails = authentication.getPrincipal();

        if (userDetails == null) {
            Message.error(response, HttpServletResponse.SC_UNAUTHORIZED, "User not found");
            return;
        }

        boolean hasRole = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(accessTypeList::contains);

        if (!hasRole) {
            Message.error(response, HttpServletResponse.SC_UNAUTHORIZED, "Permission denied");
            return;
        }

        request.setAttribute("userDetails", userDetails);
        chain.doFilter(req, res);
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return authHeader.substring("Bearer ".length());
    }

}


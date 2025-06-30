package io.github.gabrielmmoraes1999.apiservice.auth;

import io.github.gabrielmmoraes1999.apiservice.security.SecurityRule;
import io.github.gabrielmmoraes1999.apiservice.teste.SecurityFilterChain;
import io.github.gabrielmmoraes1999.apiservice.util.JwtTokenValidator;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class SecurityFilter implements Filter {

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

        Optional<SecurityRule> ruleOpt = SecurityFilterChain.getRule(path, method); //SecurityConfig

        if (!ruleOpt.isPresent()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado");
            return;
        }

        SecurityRule rule = ruleOpt.get();
        switch (rule.getAccessType()) {
            case PERMIT_ALL:
                chain.doFilter(req, res);
                return;

            case AUTHENTICATED:
            case HAS_ROLE:
                String token = recoverToken(request);
                if (token == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token ausente");
                    return;
               }

                String subject = JwtTokenValidator.validateToken(token);
                System.out.println(subject);
                if (subject == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
                    return;
                }

//                UserDetails userDetails = loginRepository.findByEmail(subject);
//                if (userDetails == null) {
//                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuário não encontrado");
//                    return;
//                }
//
//                if (rule.getAccessType() == SecurityRule.AccessType.HAS_ROLE) {
//                    boolean hasRole = userDetails.getAuthorities().stream()
//                            .map(GrantedAuthority::getAuthority)
//                            .anyMatch(rule.getRoles()::contains);
//
//                    if (!hasRole) {
//                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Permissão negada");
//                        return;
//                    }
                }
//
//                // OK
//                request.setAttribute("user", userDetails);
                chain.doFilter(req, res);
                return;
//        }
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return authHeader.substring("Bearer ".length());
    }

}
package io.github.gabrielmmoraes1999.service.websocket.config;

import io.github.gabrielmmoraes1999.apiservice.security.Authentication;
import io.github.gabrielmmoraes1999.apiservice.security.GrantedAuthority;
import io.github.gabrielmmoraes1999.apiservice.security.SecurityContextHolder;
import io.github.gabrielmmoraes1999.apiservice.security.UserDetails;
import io.github.gabrielmmoraes1999.apiservice.websocket.annotation.HandshakeInterceptor;
import io.github.gabrielmmoraes1999.service.enu.UserRole;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;

public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    public static final String PRINCIPAL_ATTRIBUTE = "principal";

    @Override
    public boolean beforeHandshake(
            HttpServletRequest request,
            HttpServletResponse response,
            Map<String, Object> attributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principal = authentication == null ? null : authentication.getPrincipal();
        if (principal == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        if (!hasRole(principal, UserRole.ADMIN.name())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        attributes.put(PRINCIPAL_ATTRIBUTE, principal);
        return true;
    }

    @Override
    public void afterHandshake(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception ex) {

    }

    private static boolean hasRole(UserDetails principal, String role) {
        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();
        if (authorities == null) {
            return false;
        }
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }

}

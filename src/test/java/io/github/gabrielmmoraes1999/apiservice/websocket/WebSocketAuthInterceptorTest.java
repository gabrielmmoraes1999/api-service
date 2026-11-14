package io.github.gabrielmmoraes1999.apiservice.websocket;

import io.github.gabrielmmoraes1999.apiservice.security.GrantedAuthority;
import io.github.gabrielmmoraes1999.apiservice.security.SecurityContextHolder;
import io.github.gabrielmmoraes1999.apiservice.security.SimpleGrantedAuthority;
import io.github.gabrielmmoraes1999.apiservice.security.UserDetails;
import io.github.gabrielmmoraes1999.apiservice.security.web.UsernamePasswordAuthenticationToken;
import io.github.gabrielmmoraes1999.service.websocket.config.WebSocketAuthInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebSocketAuthInterceptorTest {

    private final WebSocketAuthInterceptor interceptor = new WebSocketAuthInterceptor();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void anonymousHandshakeIsRefused() throws Exception {
        int[] status = {200};
        Map<String, Object> attributes = new HashMap<>();

        boolean accepted = interceptor.beforeHandshake(request(), response(status), attributes);

        assertFalse(accepted);
        assertEquals(401, status[0]);
        assertTrue(attributes.isEmpty());
    }

    @Test
    void userRoleIsRefused() throws Exception {
        UserDetails user = user("USER");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        int[] status = {200};
        Map<String, Object> attributes = new HashMap<>();

        boolean accepted = interceptor.beforeHandshake(request(), response(status), attributes);

        assertFalse(accepted);
        assertEquals(403, status[0]);
        assertTrue(attributes.isEmpty());
    }

    @Test
    void adminHandshakeCopiesPrincipal() throws Exception {
        UserDetails admin = user("ADMIN");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities()));
        int[] status = {200};
        Map<String, Object> attributes = new HashMap<>();

        boolean accepted = interceptor.beforeHandshake(request(), response(status), attributes);

        assertTrue(accepted);
        assertEquals(200, status[0]);
        assertSame(admin, attributes.get(WebSocketAuthInterceptor.PRINCIPAL_ATTRIBUTE));
    }

    private static HttpServletRequest request() {
        return (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "toString" -> "request";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> null;
                });
    }

    private static HttpServletResponse response(int[] status) {
        return (HttpServletResponse) Proxy.newProxyInstance(
                HttpServletResponse.class.getClassLoader(),
                new Class<?>[]{HttpServletResponse.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "setStatus" -> {
                        status[0] = (int) args[0];
                        yield null;
                    }
                    case "getStatus" -> status[0];
                    case "toString" -> "response";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> null;
                });
    }

    private static UserDetails user(String role) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return authorities;
            }

            @Override
            public String getPassword() {
                return null;
            }

            @Override
            public String getUsername() {
                return role.toLowerCase();
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        };
    }

}

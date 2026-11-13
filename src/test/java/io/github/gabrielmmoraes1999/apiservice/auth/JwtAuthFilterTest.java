package io.github.gabrielmmoraes1999.apiservice.auth;

import io.github.gabrielmmoraes1999.apiservice.context.ApplicationContext;
import io.github.gabrielmmoraes1999.apiservice.http.HttpMethod;
import io.github.gabrielmmoraes1999.apiservice.security.GrantedAuthority;
import io.github.gabrielmmoraes1999.apiservice.security.SecurityContextHolder;
import io.github.gabrielmmoraes1999.apiservice.security.SecurityRule;
import io.github.gabrielmmoraes1999.apiservice.security.SimpleGrantedAuthority;
import io.github.gabrielmmoraes1999.apiservice.security.UserDetails;
import io.github.gabrielmmoraes1999.apiservice.security.jwt.ProviderJwt;
import io.github.gabrielmmoraes1999.apiservice.security.web.AuthorizationBuilder;
import io.github.gabrielmmoraes1999.apiservice.security.web.HttpSecurity;
import io.github.gabrielmmoraes1999.apiservice.security.web.SecurityFilterChain;
import io.github.gabrielmmoraes1999.apiservice.security.web.UsernamePasswordAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtAuthFilterTest {

    private static final String SECRET = "unit-test-secret-key-32chars!!";

    private final JwtAuthFilter filter = new JwtAuthFilter();
    private ProviderJwt jwt;

    @BeforeEach
    void setUp() {
        jwt = new ProviderJwt(SECRET, "test-issuer");
        ApplicationContext.addBean(ProviderJwt.class, jwt);
    }

    @AfterEach
    void tearDown() {
        new SecurityFilterChain(null);
        ApplicationContext.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    void anyRequestAuthenticatedRegistersCatchAll() {
        AuthorizationBuilder builder = new AuthorizationBuilder();
        builder.authenticated();
        assertTrue(builder.getRules().isEmpty());

        builder.antMatchers("/public/**").permitAll()
                .antMatchers("/api/user/**").hasAnyRole("ADMIN")
                .anyRequest()
                .authenticated();

        List<SecurityRule> rules = builder.getRules();
        assertEquals(3, rules.size());
        assertEquals(SecurityRule.AccessType.PERMIT_ALL, rules.get(0).getAccessType());
        assertEquals(SecurityRule.AccessType.HAS_ROLE, rules.get(1).getAccessType());
        assertEquals(SecurityRule.AccessType.AUTHENTICATED, rules.get(2).getAccessType());
        assertTrue(rules.get(2).getRoles().isEmpty());
        assertTrue(rules.get(2).matches("/other/", "*"));
    }

    @Test
    void permitAllInFrontOfCatchAllDoesNotRequireToken() throws Exception {
        demoChain();

        Outcome outcome = invoke("GET", "/public/health", null, null);

        assertTrue(outcome.continued);
        assertEquals(200, outcome.status);
    }

    @Test
    void roleRouteStillRequiresThatRole() throws Exception {
        demoChain();
        String token = jwt.generateToken("admin", 120);

        Outcome allowed = invoke("GET", "/api/user/1", token, user("ADMIN"));
        assertTrue(allowed.continued);
        assertEquals(200, allowed.status);
        assertEquals("admin", ((UserDetails) allowed.userDetails).getUsername());

        Outcome denied = invoke("GET", "/api/user/1", token, user("USER"));
        assertFalse(denied.continued);
        assertEquals(403, denied.status);
        assertTrue(denied.body.contains("Permission denied"));
    }

    @Test
    void exactUserPathStillMatchesWhenFilterAppendsSlash() throws Exception {
        demoChain();
        String token = jwt.generateToken("admin", 120);

        Outcome outcome = invoke("GET", "/api/user", token, user("ADMIN"));

        assertTrue(outcome.continued);
        assertEquals(200, outcome.status);
    }

    @Test
    void authenticatedCatchAllDoesNotRequireARole() throws Exception {
        demoChain();
        String token = jwt.generateToken("user", 120);

        Outcome outcome = invoke("GET", "/other", token, user("USER"));

        assertTrue(outcome.continued);
        assertEquals(200, outcome.status);
    }

    @Test
    void authenticatedRouteRejectsMissingOrInvalidTokenAndMissingUser() throws Exception {
        demoChain();

        Outcome missingToken = invoke("GET", "/other", null, user("USER"));
        assertFalse(missingToken.continued);
        assertEquals(401, missingToken.status);
        assertTrue(missingToken.body.contains("Token away"));

        Outcome invalidToken = invoke("GET", "/other", "not-a-jwt", user("USER"));
        assertFalse(invalidToken.continued);
        assertEquals(401, invalidToken.status);
        assertTrue(invalidToken.body.contains("Token invalid"));

        Outcome missingUser = invoke("GET", "/other", jwt.generateToken("user", 120), null);
        assertFalse(missingUser.continued);
        assertEquals(401, missingUser.status);
        assertTrue(missingUser.body.contains("User not found"));
    }

    @Test
    void unmatchedPathStaysForbiddenWhenChainExists() throws Exception {
        new HttpSecurity().authorizeHttpRequests(auth -> auth
                .antMatchers("/api/user/**").hasAnyRole("ADMIN")
        ).build();

        Outcome outcome = invoke("GET", "/other", jwt.generateToken("admin", 120), user("ADMIN"));

        assertFalse(outcome.continued);
        assertEquals(403, outcome.status);
        assertTrue(outcome.body.contains("Acesso negado"));
    }

    @Test
    void missingChainStaysOpen() throws Exception {
        new SecurityFilterChain(null);

        Outcome outcome = invoke("GET", "/other", null, null);

        assertTrue(outcome.continued);
        assertEquals(200, outcome.status);
    }

    @Test
    void postTokenShortcutStaysOpen() throws Exception {
        demoChain();

        Outcome post = invoke("POST", "/oauth2/token", null, null);
        assertTrue(post.continued);
        assertEquals(200, post.status);

        Outcome get = invoke("GET", "/oauth2/token", null, null);
        assertFalse(get.continued);
        assertEquals(401, get.status);
    }

    @Test
    void methodSpecificPermitAllDoesNotOpenOtherMethods() throws Exception {
        new HttpSecurity().authorizeHttpRequests(auth -> auth
                .antMatchers(HttpMethod.GET, "/public/**").permitAll()
                .anyRequest()
                .authenticated()
        ).build();

        Outcome get = invoke("GET", "/public/health", null, null);
        assertTrue(get.continued);

        Outcome post = invoke("POST", "/public/health", null, null);
        assertFalse(post.continued);
        assertEquals(401, post.status);
    }

    private static void demoChain() {
        new HttpSecurity().authorizeHttpRequests(auth -> auth
                .antMatchers("/public/**").permitAll()
                .antMatchers("/api/user/**").hasAnyRole("ADMIN")
                .anyRequest()
                .authenticated()
        ).build();
    }

    private Outcome invoke(String method, String uri, String token, UserDetails user) throws Exception {
        if (user != null) {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        } else {
            SecurityContextHolder.clearContext();
        }

        Map<String, Object> attributes = new HashMap<>();
        HttpServletRequest request = (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                (proxy, invoked, args) -> switch (invoked.getName()) {
                    case "getRequestURI" -> uri;
                    case "getMethod" -> method;
                    case "getHeader" -> authorizationHeader(args, token);
                    case "setAttribute" -> {
                        attributes.put((String) args[0], args[1]);
                        yield null;
                    }
                    case "getAttribute" -> attributes.get(args[0]);
                    case "toString" -> uri;
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> defaultValue(invoked.getReturnType());
                });

        int[] status = {200};
        StringWriter body = new StringWriter();
        PrintWriter writer = new PrintWriter(body, true);
        HttpServletResponse response = (HttpServletResponse) Proxy.newProxyInstance(
                HttpServletResponse.class.getClassLoader(),
                new Class<?>[]{HttpServletResponse.class},
                (proxy, invoked, args) -> switch (invoked.getName()) {
                    case "setStatus" -> {
                        status[0] = (int) args[0];
                        yield null;
                    }
                    case "getStatus" -> status[0];
                    case "getWriter" -> writer;
                    case "setContentType" -> null;
                    case "toString" -> "response";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> defaultValue(invoked.getReturnType());
                });

        boolean[] continued = {false};
        FilterChain chain = (req, res) -> continued[0] = true;
        filter.doFilter(request, response, chain);
        return new Outcome(continued[0], status[0], body.toString(), attributes.get("userDetails"));
    }

    private static Object authorizationHeader(Object[] args, String token) {
        if (token == null || args == null || args.length == 0 || !"Authorization".equals(args[0])) {
            return null;
        }
        return "Bearer " + token;
    }

    private static Object defaultValue(Class<?> type) {
        if (type == boolean.class) {
            return false;
        }
        if (type == byte.class) {
            return (byte) 0;
        }
        if (type == short.class) {
            return (short) 0;
        }
        if (type == int.class) {
            return 0;
        }
        if (type == long.class) {
            return 0L;
        }
        if (type == float.class) {
            return 0f;
        }
        if (type == double.class) {
            return 0d;
        }
        if (type == char.class) {
            return '\0';
        }
        return null;
    }

    private static UserDetails user(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
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

    private static final class Outcome {
        private final boolean continued;
        private final int status;
        private final String body;
        private final Object userDetails;

        private Outcome(boolean continued, int status, String body, Object userDetails) {
            this.continued = continued;
            this.status = status;
            this.body = body;
            this.userDetails = userDetails;
        }
    }

}

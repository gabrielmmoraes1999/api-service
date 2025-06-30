package io.github.gabrielmmoraes1999.service.config;

import io.github.gabrielmmoraes1999.apiservice.annotation.Component;
import io.github.gabrielmmoraes1999.apiservice.security.Authentication;
import io.github.gabrielmmoraes1999.apiservice.security.SecurityContextHolder;
import io.github.gabrielmmoraes1999.apiservice.security.UserDetails;
import io.github.gabrielmmoraes1999.apiservice.security.web.UsernamePasswordAuthenticationToken;
import io.github.gabrielmmoraes1999.apiservice.util.JwtTokenValidator;
import io.github.gabrielmmoraes1999.apiservice.security.web.OncePerRequestFilter;
import io.github.gabrielmmoraes1999.service.entity.Login;
import io.github.gabrielmmoraes1999.service.entity.LoginRole;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    //@Autowired
    //LoginRepository loginRepository;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = this.recoverToken(request);
        if(token != null) {
            String subject = JwtTokenValidator.validateToken(token);

            if (subject != null) {
                Login login = new Login();
                login.setUuid("123");
                login.setEmail("admin");
                login.setPassword("123");
                login.setRole(LoginRole.ADMIN);
                UserDetails userDetails = login;

                if (Objects.nonNull(userDetails)) {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }

}


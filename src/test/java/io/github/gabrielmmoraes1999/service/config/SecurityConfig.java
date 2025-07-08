package io.github.gabrielmmoraes1999.service.config;

import io.github.gabrielmmoraes1999.apiservice.annotation.Bean;
import io.github.gabrielmmoraes1999.apiservice.annotation.Configuration;
import io.github.gabrielmmoraes1999.apiservice.annotation.EnableWebSecurity;
import io.github.gabrielmmoraes1999.apiservice.auth.JwtAuthFilter;
import io.github.gabrielmmoraes1999.apiservice.http.HttpMethod;
import io.github.gabrielmmoraes1999.apiservice.security.web.HttpSecurity;
import io.github.gabrielmmoraes1999.apiservice.security.web.SecurityFilterChain;
import io.github.gabrielmmoraes1999.service.enu.UserRole;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter();
    }

    @Bean
    public SecurityFilterChain authFilterChain(HttpSecurity httpSecurity) {
        return httpSecurity.authorizeHttpRequests(auth -> auth
                .antMatchers("/websocket/**").hasAnyRole(UserRole.ADMIN.name())
                .antMatchers("/api/user/**").hasAnyRole(UserRole.ADMIN.name())
                .antMatchers("/api/client/**").hasAnyRole(UserRole.ADMIN.name())
                .antMatchers(HttpMethod.GET, "/api/client/**").hasAnyRole(UserRole.USER.name())
                .antMatchers(HttpMethod.POST, "/api/client/**").hasAnyRole(UserRole.USER.name())
                .anyRequest()
                .authenticated()
        ).build();
    }

}
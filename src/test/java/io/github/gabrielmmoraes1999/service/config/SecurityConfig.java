package io.github.gabrielmmoraes1999.service.config;

import io.github.gabrielmmoraes1999.apiservice.annotation.Bean;
import io.github.gabrielmmoraes1999.apiservice.annotation.Configuration;
import io.github.gabrielmmoraes1999.apiservice.annotation.EnableWebSecurity;
import io.github.gabrielmmoraes1999.apiservice.auth.JwtAuthFilter;
import io.github.gabrielmmoraes1999.apiservice.http.HttpMethod;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.RefreshTokenAuthenticationMethod;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.RefreshTokenFormat;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.TokenSettings;
import io.github.gabrielmmoraes1999.apiservice.security.web.HttpSecurity;
import io.github.gabrielmmoraes1999.apiservice.security.web.SecurityFilterChain;
import io.github.gabrielmmoraes1999.service.enu.UserRole;

import java.time.Duration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter();
    }

    @Bean
    public TokenSettings tokenSettings() {
        return TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofHours(6))
                .refreshTokenTimeToLive(Duration.ofDays(30))
                .refreshTokenAuthenticationMethod(RefreshTokenAuthenticationMethod.REFRESH_TOKEN_ONLY)
                .refreshTokenFormat(RefreshTokenFormat.OPAQUE_32_BASE64_URL)
                .build();
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
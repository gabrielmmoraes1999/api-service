package io.github.gabrielmmoraes1999.service.config;

import io.github.gabrielmmoraes1999.apiservice.annotation.Bean;
import io.github.gabrielmmoraes1999.apiservice.annotation.Configuration;
import io.github.gabrielmmoraes1999.apiservice.annotation.EnableWebSecurity;
import io.github.gabrielmmoraes1999.apiservice.auth.JwtAuthFilter;
import io.github.gabrielmmoraes1999.apiservice.http.HttpMethod;
import io.github.gabrielmmoraes1999.apiservice.security.web.HttpSecurity;
import io.github.gabrielmmoraes1999.apiservice.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

//    @Bean
//    public BasicAuthFilter basicAuthFilter() {
//        return new BasicAuthFilter("admin", "admin");
//    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter();
    }

    @Bean
    public SecurityFilterChain authFilterChain(HttpSecurity httpSecurity) {
        return httpSecurity.authorizeHttpRequests(auth -> auth
                        .antMatchers("/websocket/**").permitAll()
                        .antMatchers(null, "/api/users/**").hasAnyRole("ADMIN")
                        .antMatchers(HttpMethod.GET, "/updates/**").permitAll()
                        .anyRequest()
                        .authenticated()
                ).build();
    }

}
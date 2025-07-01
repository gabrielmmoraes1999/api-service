package io.github.gabrielmmoraes1999.apiservice.controller;

import io.github.gabrielmmoraes1999.apiservice.annotation.PostMapping;
import io.github.gabrielmmoraes1999.apiservice.annotation.RequestHeader;
import io.github.gabrielmmoraes1999.apiservice.annotation.RestController;
import io.github.gabrielmmoraes1999.apiservice.http.HttpStatus;
import io.github.gabrielmmoraes1999.apiservice.http.ResponseEntity;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.RegisteredClient;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.RegisteredClientJDBC;
import io.github.gabrielmmoraes1999.apiservice.util.JwtTokenValidator;

import java.time.Duration;
import java.util.*;

@RestController
public class OAuth2TokenController {

    @PostMapping("/oauth2/token")
    public ResponseEntity<?> getToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED, "Missing Authorization Header");
        }

        String base64Credentials = authHeader.substring("Basic ".length());
        String credentials = new String(Base64.getDecoder().decode(base64Credentials));
        String[] parts = credentials.split(":", 2);

        if (parts.length != 2)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED, "Invalid auth");

        String username = parts[0];
        String password = parts[1];
        RegisteredClient registeredClient = RegisteredClientJDBC.findByClientIdAndClientSecret(username, password);

        if (registeredClient != null) {
            Duration accessTokenTimeToLive = registeredClient.getTokenSettings().getAccessTokenTimeToLive();
            String token = JwtTokenValidator.generateJwtToken(username, accessTokenTimeToLive.getSeconds());

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("access_token", token);
            response.put("token_type", "Bearer");
            response.put("expires_in", accessTokenTimeToLive.getSeconds());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }

}

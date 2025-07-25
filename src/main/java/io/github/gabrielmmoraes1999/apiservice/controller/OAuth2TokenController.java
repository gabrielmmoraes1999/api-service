package io.github.gabrielmmoraes1999.apiservice.controller;

import io.github.gabrielmmoraes1999.apiservice.annotation.PostMapping;
import io.github.gabrielmmoraes1999.apiservice.annotation.RequestHeader;
import io.github.gabrielmmoraes1999.apiservice.annotation.RestController;
import io.github.gabrielmmoraes1999.apiservice.context.ApplicationContext;
import io.github.gabrielmmoraes1999.apiservice.http.HttpStatus;
import io.github.gabrielmmoraes1999.apiservice.http.ResponseEntity;
import io.github.gabrielmmoraes1999.apiservice.security.crypto.PasswordEncoder;
import io.github.gabrielmmoraes1999.apiservice.security.crypto.md5.Md5PasswordEncoder;
import io.github.gabrielmmoraes1999.apiservice.security.jwt.ProviderJwt;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.RegisteredClient;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.RegisteredClientJDBC;
import io.github.gabrielmmoraes1999.apiservice.utils.Message;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@RestController
public class OAuth2TokenController {

    @PostMapping("/oauth2/token")
    public ResponseEntity<?> getToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED, Message.error("Missing Authorization Header"));
        }

        String base64Credentials = authHeader.substring("Basic ".length());
        String credentials = new String(Base64.getDecoder().decode(base64Credentials));
        String[] usernameAndPassword = credentials.split(":", 2);

        if (usernameAndPassword.length != 2)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED, Message.error("Invalid auth"));

        RegisteredClient registeredClient = RegisteredClientJDBC.findByClientId(usernameAndPassword[0]);

        if (registeredClient != null) {
            if (ApplicationContext.getBean(PasswordEncoder.class, new Md5PasswordEncoder())
                    .matches(usernameAndPassword[1], registeredClient.getClientSecret())) {

                Instant now = Instant.now();
                Duration accessTokenTimeToLive = registeredClient.getTokenSettings().getAccessTokenTimeToLive();
                ProviderJwt providerJwt = ApplicationContext.getBean(ProviderJwt.class, new ProviderJwt());
                String token = providerJwt.generateToken(registeredClient.getId(), accessTokenTimeToLive.getSeconds());

                RegisteredClientJDBC.saveToken(
                        registeredClient.getId(),
                        registeredClient.getClientId(),
                        Timestamp.from(now),
                        Timestamp.from(now.plusSeconds(accessTokenTimeToLive.getSeconds())),
                        "Bearer",
                        token
                );

                Map<String, Object> response = new LinkedHashMap<>();
                response.put("access_token", token);
                response.put("token_type", "Bearer");
                response.put("expires_in", accessTokenTimeToLive.getSeconds());

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED, Message.error("Invalid credentials"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED, Message.error("Invalid credentials"));
        }
    }

}

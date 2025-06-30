package io.github.gabrielmmoraes1999.apiservice.controller;

import io.github.gabrielmmoraes1999.apiservice.annotation.PostMapping;
import io.github.gabrielmmoraes1999.apiservice.annotation.RequestHeader;
import io.github.gabrielmmoraes1999.apiservice.annotation.RestController;
import io.github.gabrielmmoraes1999.apiservice.http.HttpStatus;
import io.github.gabrielmmoraes1999.apiservice.http.ResponseEntity;
import io.github.gabrielmmoraes1999.apiservice.util.JwtTokenValidator;

import java.util.*;

@RestController
public class OAuth2TokenController {

    public static int expiresIn = 21600;

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

        if (validateUser(username, password)) {
            String token = JwtTokenValidator.generateJwtToken(username, expiresIn);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("access_token", token);
            response.put("token_type", "Bearer");
            response.put("expires_in", expiresIn);

            return ResponseEntity.ok(response);
            //return ResponseEntity.ok(Collections.singletonMap("access_token", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }

    private boolean validateUser(String user, String pass) {
        // Aqui você pode consultar um banco de dados ou usar valores fixos
        return user.equals("admin") && pass.equals("123456");
    }

}

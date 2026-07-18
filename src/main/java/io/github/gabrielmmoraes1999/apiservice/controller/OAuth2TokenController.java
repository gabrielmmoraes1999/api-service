package io.github.gabrielmmoraes1999.apiservice.controller;

import io.github.gabrielmmoraes1999.apiservice.annotation.PostMapping;
import io.github.gabrielmmoraes1999.apiservice.annotation.RequestHeader;
import io.github.gabrielmmoraes1999.apiservice.annotation.RequestParam;
import io.github.gabrielmmoraes1999.apiservice.annotation.RestController;
import io.github.gabrielmmoraes1999.apiservice.context.ApplicationContext;
import io.github.gabrielmmoraes1999.apiservice.http.HttpStatus;
import io.github.gabrielmmoraes1999.apiservice.http.ResponseEntity;
import io.github.gabrielmmoraes1999.apiservice.security.crypto.PasswordEncoder;
import io.github.gabrielmmoraes1999.apiservice.security.crypto.md5.Md5PasswordEncoder;
import io.github.gabrielmmoraes1999.apiservice.security.jwt.ProviderJwt;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.OAuth2Authorization;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.OpaqueTokenGenerator;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.RefreshTokenAuthenticationMethod;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.RefreshTokenFormat;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.RegisteredClient;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.RegisteredClientJDBC;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.TokenSettings;
import io.github.gabrielmmoraes1999.apiservice.utils.Message;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class OAuth2TokenController {

    @PostMapping("/oauth2/token")
    public ResponseEntity<?> getToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam("grant_type") String grantType,
            @RequestParam("refresh_token") String refreshToken,
            @RequestParam("client_id") String clientId
    ) {
        if (grantType == null || grantType.isBlank() || "client_credentials".equals(grantType)) {
            return issueToken(authHeader);
        }

        if ("refresh_token".equals(grantType)) {
            return refreshToken(authHeader, refreshToken, clientId);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST, Message.error("Unsupported grant_type"));
    }

    private ResponseEntity<?> issueToken(String authHeader) {
        RegisteredClient registeredClient = authenticateWithClientSecret(authHeader);
        if (registeredClient == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED, Message.error("Invalid credentials"));
        }

        return buildTokenResponse(registeredClient);
    }

    private ResponseEntity<?> refreshToken(String authHeader, String refreshToken, String clientIdParam) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST, Message.error("Missing refresh_token"));
        }

        OAuth2Authorization authorization = RegisteredClientJDBC.findAuthorizationByRefreshToken(refreshToken);
        if (authorization == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED, Message.error("Invalid refresh_token"));
        }

        if (authorization.getRefreshTokenExpiresAt() != null
                && authorization.getRefreshTokenExpiresAt().toInstant().isBefore(Instant.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED, Message.error("Expired refresh_token"));
        }

        RegisteredClient registeredClient = RegisteredClientJDBC.findById(authorization.getRegisteredClientId());
        if (registeredClient == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED, Message.error("Invalid credentials"));
        }

        Duration refreshTokenTimeToLive = registeredClient.getTokenSettings().getRefreshTokenTimeToLive();
        RefreshTokenAuthenticationMethod authenticationMethod =
                registeredClient.getTokenSettings().getRefreshTokenAuthenticationMethod();

        if (refreshTokenTimeToLive == null || authenticationMethod == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED, Message.error("Refresh token not supported for this client"));
        }

        if (authenticationMethod == RefreshTokenAuthenticationMethod.CLIENT_SECRET_BASIC) {
            RegisteredClient authenticated = authenticateWithClientSecret(authHeader);
            if (authenticated == null || !authenticated.getId().equals(registeredClient.getId())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED, Message.error("Invalid credentials"));
            }
        } else if (authenticationMethod == RefreshTokenAuthenticationMethod.CLIENT_ID_ONLY) {
            if (clientIdParam == null || clientIdParam.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST, Message.error("Missing client_id"));
            }
            if (!clientIdParam.equals(registeredClient.getClientId())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED, Message.error("Invalid credentials"));
            }
        } else if (authenticationMethod != RefreshTokenAuthenticationMethod.REFRESH_TOKEN_ONLY) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED, Message.error("Invalid credentials"));
        }

        RegisteredClientJDBC.invalidateRefreshToken(refreshToken);
        return buildTokenResponse(registeredClient);
    }

    private RegisteredClient authenticateWithClientSecret(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return null;
        }

        try {
            String base64Credentials = authHeader.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            String[] usernameAndPassword = credentials.split(":", 2);

            if (usernameAndPassword.length != 2) {
                return null;
            }

            RegisteredClient registeredClient = RegisteredClientJDBC.findByClientId(usernameAndPassword[0]);
            if (registeredClient == null) {
                return null;
            }

            boolean matches = ApplicationContext.getBean(PasswordEncoder.class, new Md5PasswordEncoder())
                    .matches(usernameAndPassword[1], registeredClient.getClientSecret());

            return matches ? registeredClient : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private ResponseEntity<?> buildTokenResponse(RegisteredClient registeredClient) {
        Instant now = Instant.now();
        TokenSettings tokenSettings = registeredClient.getTokenSettings();
        Duration accessTokenTimeToLive = tokenSettings.getAccessTokenTimeToLive();
        Duration refreshTokenTimeToLive = tokenSettings.getRefreshTokenTimeToLive();
        RefreshTokenAuthenticationMethod refreshTokenAuthenticationMethod =
                tokenSettings.getRefreshTokenAuthenticationMethod();

        ProviderJwt providerJwt = ApplicationContext.getBean(ProviderJwt.class, new ProviderJwt());
        String accessToken = providerJwt.generateToken(registeredClient.getId(), accessTokenTimeToLive.getSeconds());

        String refreshTokenValue = null;
        Timestamp refreshTokenIssuedAt = null;
        Timestamp refreshTokenExpiresAt = null;

        if (refreshTokenTimeToLive != null && refreshTokenAuthenticationMethod != null) {
            RefreshTokenFormat refreshTokenFormat = tokenSettings.getRefreshTokenFormat();
            if (refreshTokenFormat == RefreshTokenFormat.JWT) {
                refreshTokenValue = providerJwt.generateToken(
                        "refresh_token:" + registeredClient.getId(),
                        refreshTokenTimeToLive.getSeconds()
                );
            } else if (refreshTokenFormat.isOpaque()) {
                refreshTokenValue = OpaqueTokenGenerator.generate(
                        refreshTokenFormat.getOpaqueSizeBytes(),
                        refreshTokenFormat.getOpaqueEncoding()
                );
            } else {
                refreshTokenValue = UUID.randomUUID().toString();
            }
            refreshTokenIssuedAt = Timestamp.from(now);
            refreshTokenExpiresAt = Timestamp.from(now.plusSeconds(refreshTokenTimeToLive.getSeconds()));
        }

        RegisteredClientJDBC.saveToken(
                registeredClient.getId(),
                registeredClient.getClientId(),
                Timestamp.from(now),
                Timestamp.from(now.plusSeconds(accessTokenTimeToLive.getSeconds())),
                "Bearer",
                accessToken,
                refreshTokenValue,
                refreshTokenIssuedAt,
                refreshTokenExpiresAt
        );

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("access_token", accessToken);
        response.put("token_type", "Bearer");
        response.put("expires_in", accessTokenTimeToLive.getSeconds());

        if (refreshTokenValue != null) {
            response.put("refresh_token", refreshTokenValue);
            response.put("refresh_expires_in", refreshTokenTimeToLive.getSeconds());
        }

        return ResponseEntity.ok(response);
    }

}

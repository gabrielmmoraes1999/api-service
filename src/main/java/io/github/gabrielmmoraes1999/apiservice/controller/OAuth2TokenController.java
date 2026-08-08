package io.github.gabrielmmoraes1999.apiservice.controller;

import io.github.gabrielmmoraes1999.apiservice.annotation.PostMapping;
import io.github.gabrielmmoraes1999.apiservice.annotation.RequestHeader;
import io.github.gabrielmmoraes1999.apiservice.annotation.RequestParam;
import io.github.gabrielmmoraes1999.apiservice.annotation.RestController;
import io.github.gabrielmmoraes1999.apiservice.context.ApplicationContext;
import io.github.gabrielmmoraes1999.apiservice.http.HttpStatus;
import io.github.gabrielmmoraes1999.apiservice.http.ResponseEntity;
import io.github.gabrielmmoraes1999.apiservice.security.crypto.PasswordEncoder;
import io.github.gabrielmmoraes1999.apiservice.security.crypto.bcrypt.BCryptPasswordEncoder;
import io.github.gabrielmmoraes1999.apiservice.security.jwt.ProviderJwt;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.OAuth2Authorization;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.OAuth2TokenEvents;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.OAuth2TokenSuccessContext;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.OpaqueTokenGenerator;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.RefreshTokenAuthenticationMethod;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.RefreshTokenFormat;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.RegisteredClient;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.RegisteredClientJDBC;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.TokenSettings;
import io.github.gabrielmmoraes1999.apiservice.utils.Message;
import jakarta.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;
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
            @RequestParam("client_id") String clientId,
            HttpServletRequest request
    ) {
        Map<String, String> parameters = toParameterMap(request);
        String remoteAddress = resolveRemoteAddress(request);

        if (grantType == null || grantType.isBlank() || "client_credentials".equals(grantType)) {
            return issueToken(authHeader, resolveGrantType(grantType), remoteAddress, parameters);
        }

        if ("refresh_token".equals(grantType)) {
            return refreshToken(authHeader, refreshToken, clientId, grantType, remoteAddress, parameters);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST, Message.error("Unsupported grant_type"));
    }

    private ResponseEntity<?> issueToken(
            String authHeader,
            String grantType,
            String remoteAddress,
            Map<String, String> parameters
    ) {
        RegisteredClient registeredClient = authenticateWithClientSecret(authHeader);
        if (registeredClient == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED, Message.error("Invalid credentials"));
        }

        return buildTokenResponse(registeredClient, null, grantType, remoteAddress, parameters);
    }

    private ResponseEntity<?> refreshToken(
            String authHeader,
            String refreshToken,
            String clientIdParam,
            String grantType,
            String remoteAddress,
            Map<String, String> parameters
    ) {
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

        return buildTokenResponse(registeredClient, refreshToken, grantType, remoteAddress, parameters);
    }

    private RegisteredClient authenticateWithClientSecret(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return null;
        }

        try {
            String base64Credentials = authHeader.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
            String[] usernameAndPassword = credentials.split(":", 2);

            if (usernameAndPassword.length != 2) {
                return null;
            }

            RegisteredClient registeredClient = RegisteredClientJDBC.findByClientId(usernameAndPassword[0]);
            if (registeredClient == null) {
                return null;
            }

            boolean matches = ApplicationContext.getBean(PasswordEncoder.class, new BCryptPasswordEncoder())
                    .matches(usernameAndPassword[1], registeredClient.getClientSecret());

            return matches ? registeredClient : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private ResponseEntity<?> buildTokenResponse(
            RegisteredClient registeredClient,
            String oldRefreshToken,
            String grantType,
            String remoteAddress,
            Map<String, String> parameters
    ) {
        Instant now = Instant.now();
        TokenSettings tokenSettings = registeredClient.getTokenSettings();
        Duration accessTokenTimeToLive = tokenSettings.getAccessTokenTimeToLive();
        Duration refreshTokenTimeToLive = tokenSettings.getRefreshTokenTimeToLive();
        RefreshTokenAuthenticationMethod refreshTokenAuthenticationMethod =
                tokenSettings.getRefreshTokenAuthenticationMethod();

        ProviderJwt providerJwt = ApplicationContext.getBean(ProviderJwt.class);
        if (providerJwt == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR,
                    Message.error("ProviderJwt bean is required"));
        }

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

        Timestamp accessTokenIssuedAt = Timestamp.from(now);
        Timestamp accessTokenExpiresAt = Timestamp.from(now.plusSeconds(accessTokenTimeToLive.getSeconds()));

        if (oldRefreshToken != null) {
            RegisteredClientJDBC.rotateRefreshToken(
                    oldRefreshToken,
                    registeredClient.getId(),
                    registeredClient.getClientId(),
                    accessTokenIssuedAt,
                    accessTokenExpiresAt,
                    "Bearer",
                    accessToken,
                    refreshTokenValue,
                    refreshTokenIssuedAt,
                    refreshTokenExpiresAt
            );
        } else {
            RegisteredClientJDBC.saveToken(
                    registeredClient.getId(),
                    registeredClient.getClientId(),
                    accessTokenIssuedAt,
                    accessTokenExpiresAt,
                    "Bearer",
                    accessToken,
                    refreshTokenValue,
                    refreshTokenIssuedAt,
                    refreshTokenExpiresAt
            );
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("access_token", accessToken);
        response.put("token_type", "Bearer");
        response.put("expires_in", accessTokenTimeToLive.getSeconds());

        if (refreshTokenValue != null) {
            response.put("refresh_token", refreshTokenValue);
            response.put("refresh_expires_in", refreshTokenTimeToLive.getSeconds());
        }

        notifyTokenSuccess(registeredClient, grantType, remoteAddress, parameters, response);

        return ResponseEntity.ok(response);
    }

    private void notifyTokenSuccess(
            RegisteredClient registeredClient,
            String grantType,
            String remoteAddress,
            Map<String, String> parameters,
            Map<String, Object> tokenResponse
    ) {
        OAuth2TokenEvents.fireSuccess(new OAuth2TokenSuccessContext(
                registeredClient,
                grantType,
                remoteAddress,
                parameters,
                tokenResponse
        ));
    }

    private static String resolveGrantType(String grantType) {
        if (grantType == null || grantType.isBlank()) {
            return "client_credentials";
        }
        return grantType;
    }

    private static String resolveRemoteAddress(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            int comma = forwardedFor.indexOf(',');
            return (comma >= 0 ? forwardedFor.substring(0, comma) : forwardedFor).trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }

        return request.getRemoteAddr();
    }

    private static Map<String, String> toParameterMap(HttpServletRequest request) {
        Map<String, String> parameters = new LinkedHashMap<>();
        if (request == null) {
            return parameters;
        }

        Map<String, String[]> raw = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : raw.entrySet()) {
            String[] values = entry.getValue();
            parameters.put(entry.getKey(), values != null && values.length > 0 ? values[0] : null);
        }
        return parameters;
    }

}

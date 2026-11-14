package io.github.gabrielmmoraes1999.apiservice.http;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Resolves the client address reported on OAuth2 token events.
 * The default uses only {@link HttpServletRequest#getRemoteAddr()}.
 * Register {@link #trustProxyHeaders()} as a bean to also read
 * {@code X-Forwarded-For} and {@code X-Real-IP}.
 */
public final class ClientIp {

    private final boolean trustProxyHeaders;

    private ClientIp(boolean trustProxyHeaders) {
        this.trustProxyHeaders = trustProxyHeaders;
    }

    public static ClientIp remoteAddressOnly() {
        return new ClientIp(false);
    }

    public static ClientIp trustProxyHeaders() {
        return new ClientIp(true);
    }

    public String resolve(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        if (trustProxyHeaders) {
            String forwardedFor = request.getHeader("X-Forwarded-For");
            if (forwardedFor != null && !forwardedFor.isBlank()) {
                int comma = forwardedFor.indexOf(',');
                return (comma >= 0 ? forwardedFor.substring(0, comma) : forwardedFor).trim();
            }

            String realIp = request.getHeader("X-Real-IP");
            if (realIp != null && !realIp.isBlank()) {
                return realIp.trim();
            }
        }

        return request.getRemoteAddr();
    }

}

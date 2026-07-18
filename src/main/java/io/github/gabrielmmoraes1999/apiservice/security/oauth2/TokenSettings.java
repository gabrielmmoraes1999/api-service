package io.github.gabrielmmoraes1999.apiservice.security.oauth2;

import java.io.Serializable;
import java.time.Duration;

public class TokenSettings implements Serializable {

    private Duration accessTokenTimeToLive;
    private Duration refreshTokenTimeToLive;
    private RefreshTokenAuthenticationMethod refreshTokenAuthenticationMethod;
    private RefreshTokenFormat refreshTokenFormat;

    public static TokenSettings builder() {
        return new TokenSettings();
    }

    public TokenSettings accessTokenTimeToLive(Duration accessTokenTimeToLive) {
        this.accessTokenTimeToLive = accessTokenTimeToLive;
        return this;
    }

    public TokenSettings refreshTokenTimeToLive(Duration refreshTokenTimeToLive) {
        this.refreshTokenTimeToLive = refreshTokenTimeToLive;
        return this;
    }

    public TokenSettings refreshTokenAuthenticationMethod(RefreshTokenAuthenticationMethod refreshTokenAuthenticationMethod) {
        this.refreshTokenAuthenticationMethod = refreshTokenAuthenticationMethod;
        return this;
    }

    public TokenSettings refreshTokenFormat(RefreshTokenFormat refreshTokenFormat) {
        this.refreshTokenFormat = refreshTokenFormat;
        return this;
    }

    public Duration getAccessTokenTimeToLive() {
        return accessTokenTimeToLive;
    }

    public Duration getRefreshTokenTimeToLive() {
        return refreshTokenTimeToLive;
    }

    public RefreshTokenAuthenticationMethod getRefreshTokenAuthenticationMethod() {
        return refreshTokenAuthenticationMethod;
    }

    public RefreshTokenFormat getRefreshTokenFormat() {
        return refreshTokenFormat == null ? RefreshTokenFormat.UUID : refreshTokenFormat;
    }

    public TokenSettings build() {
        if (refreshTokenTimeToLive != null && refreshTokenAuthenticationMethod == null) {
            throw new IllegalStateException(
                    "refreshTokenAuthenticationMethod is required when refreshTokenTimeToLive is set"
            );
        }
        return this;
    }

}

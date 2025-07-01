package io.github.gabrielmmoraes1999.apiservice.security.oauth2;

import java.io.Serializable;
import java.time.Duration;

public class TokenSettings implements Serializable {

    private Duration accessTokenTimeToLive;

    public static TokenSettings builder() {
        return new TokenSettings();
    }

    public TokenSettings accessTokenTimeToLive(Duration accessTokenTimeToLive) {
        this.accessTokenTimeToLive = accessTokenTimeToLive;
        return this;
    }

    public Duration getAccessTokenTimeToLive() {
        return accessTokenTimeToLive;
    }

    public TokenSettings build() {
        return this;
    }

}

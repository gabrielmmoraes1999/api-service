package io.github.gabrielmmoraes1999.apiservice.security.oauth2;

import java.sql.Timestamp;

public class OAuth2Authorization {

    private final String registeredClientId;
    private final String principalName;
    private final Timestamp refreshTokenExpiresAt;

    public OAuth2Authorization(String registeredClientId, String principalName, Timestamp refreshTokenExpiresAt) {
        this.registeredClientId = registeredClientId;
        this.principalName = principalName;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    }

    public String getRegisteredClientId() {
        return registeredClientId;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public Timestamp getRefreshTokenExpiresAt() {
        return refreshTokenExpiresAt;
    }

}

package io.github.gabrielmmoraes1999.apiservice.security.oauth2;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class RegisteredClient {

    protected final String id;
    protected String clientId;
    protected Instant clientIdIssuedAt;
    protected String clientSecret;
    protected String clientName;
    protected TokenSettings tokenSettings;

    protected RegisteredClient(String id) {
        this.id = id;
    }

    public static RegisteredClient withId(String id) {
        return new RegisteredClient(id);
    }

    public RegisteredClient clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public RegisteredClient clientIdIssuedAt(Instant clientIdIssuedAt) {
        this.clientIdIssuedAt = clientIdIssuedAt;
        return this;
    }

    public RegisteredClient clientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public RegisteredClient clientName(String clientName) {
        this.clientName = clientName;
        return this;
    }

    public RegisteredClient tokenSettings(TokenSettings tokenSettings) {
        this.tokenSettings = tokenSettings;
        return this;
    }

    public TokenSettings getTokenSettings() {
        return tokenSettings;
    }

    public RegisteredClient build() {
        if (Objects.isNull(this.clientIdIssuedAt)) {
            this.clientIdIssuedAt = Instant.now();
        }

        if (Objects.isNull(this.clientName)) {
            this.clientName = this.id;
        }

        if (Objects.isNull(this.tokenSettings)) {
            this.tokenSettings = TokenSettings.builder()
                    .accessTokenTimeToLive(Duration.ofHours(1))
                    .build();
        }

        return this;
    }

}

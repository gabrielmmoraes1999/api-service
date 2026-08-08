package io.github.gabrielmmoraes1999.apiservice.security.oauth2;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class OAuth2TokenSuccessContext {

    private final RegisteredClient registeredClient;
    private final String grantType;
    private final String remoteAddress;
    private final Map<String, String> parameters;
    private final Map<String, Object> tokenResponse;

    public OAuth2TokenSuccessContext(
            RegisteredClient registeredClient,
            String grantType,
            String remoteAddress,
            Map<String, String> parameters,
            Map<String, Object> tokenResponse
    ) {
        this.registeredClient = registeredClient;
        this.grantType = grantType;
        this.remoteAddress = remoteAddress;
        this.parameters = parameters == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<>(parameters));
        this.tokenResponse = tokenResponse == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<>(tokenResponse));
    }

    public RegisteredClient getRegisteredClient() {
        return registeredClient;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public Map<String, Object> getTokenResponse() {
        return tokenResponse;
    }

}

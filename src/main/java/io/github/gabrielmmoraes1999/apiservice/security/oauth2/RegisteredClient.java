package io.github.gabrielmmoraes1999.apiservice.security.oauth2;

public class RegisteredClient {

    private final String id;
    private String clientId;
    private String clientSecret;
    private String clientAuthenticationMethod = "client_secret_basic";
    private String authorizationGrantType = "client_credentials";
    private Integer expiresIn;

    private RegisteredClient(String id) {
        this.id = id;
    }

    public static RegisteredClient withId(String id) {
        return new RegisteredClient(id);
    }

    public RegisteredClient clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public RegisteredClient clientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public RegisteredClient clientAuthenticationMethod(String clientAuthenticationMethod) {
        this.clientAuthenticationMethod = clientAuthenticationMethod;
        return this;
    }

    public RegisteredClient authorizationGrantType(String authorizationGrantType) {
        this.authorizationGrantType = authorizationGrantType;
        return this;
    }

    public RegisteredClient expiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    public String getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getClientAuthenticationMethod() {
        return clientAuthenticationMethod;
    }

    public String getAuthorizationGrantType() {
        return authorizationGrantType;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

}

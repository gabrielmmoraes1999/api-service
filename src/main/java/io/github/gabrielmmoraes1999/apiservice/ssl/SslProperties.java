package io.github.gabrielmmoraes1999.apiservice.ssl;

public class SslProperties {

    private final boolean enabled;
    private final String keyStorePath;
    private final String keyStorePassword;
    private final String keyPassword;
    private final int port;

    public SslProperties(boolean enabled, String keyStorePath, String keyStorePassword, String keyPassword, int port) {
        this.enabled = enabled;
        this.keyStorePath = keyStorePath;
        this.keyStorePassword = keyStorePassword;
        this.keyPassword = keyPassword;
        this.port = port;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getKeyStorePath() {
        return keyStorePath;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public String getKeyPassword() {
        return keyPassword;
    }

    public int getPort() {
        return port;
    }

}


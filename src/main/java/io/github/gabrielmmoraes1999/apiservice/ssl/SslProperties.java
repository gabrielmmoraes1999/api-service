package io.github.gabrielmmoraes1999.apiservice.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class SslProperties {

    private final boolean enabled;
    private final KeyStore keyStore;
    private final String keyStorePassword;
    private final String keyPassword;
    private final int port;

    public SslProperties(boolean enabled, String keyStoreType, String keyStorePath, String keyStorePassword, String keyPassword, int port) {
        this.enabled = enabled;

        try (InputStream is = SslProperties.class.getResourceAsStream(keyStorePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Keystore not found at: " + keyStorePath);
            }

            this.keyStore = KeyStore.getInstance(keyStoreType);
            this.keyStore.load(is, keyStorePassword.toCharArray());
        } catch (CertificateException | KeyStoreException | NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }

        this.keyStorePassword = keyStorePassword;
        this.keyPassword = keyPassword;
        this.port = port;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public KeyStore getKeyStore() {
        return keyStore;
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


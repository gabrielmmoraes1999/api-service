package io.github.gabrielmmoraes1999.service.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.gabrielmmoraes1999.apiservice.annotation.Component;
import io.github.gabrielmmoraes1999.apiservice.security.jwt.ProviderJwt;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtConfig extends ProviderJwt {

    private static final String ISSUER = "localhost";

    @Override
    public String generateToken(String subject, long expiresIn) {
        PrivateKey privateKey = loadPrivateKeyFromJKS(
                "/keystore.jks",
                "123456",
                "my-certificate"
        );

        Instant now = Instant.now();

        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(subject)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(expiresIn)))
                .sign(Algorithm.RSA256(null, (RSAPrivateKey) privateKey));
    }

    @Override
    public String checkToken(String token) {
        try {
            PublicKey publicKey = loadPublicKeyFromJKS(
                    "/keystore.jks",
                    "123456",
                    "my-certificate"
            );

            DecodedJWT jwt = JWT.require(Algorithm.RSA256((RSAPublicKey) publicKey, null))
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token);
            return jwt.getSubject();
        } catch (JWTVerificationException ex) {
            return null;
        }
    }

    private PrivateKey loadPrivateKeyFromJKS(String jksPath, String password, String alias) {
        PrivateKey privateKey = null;

        try (InputStream is = JwtConfig.class.getResourceAsStream(jksPath)) {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(is, password.toCharArray());

            KeyStore.PasswordProtection keyPassword = new KeyStore.PasswordProtection(password.toCharArray());
            KeyStore.PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, keyPassword);

            if (entry != null) {
                privateKey = entry.getPrivateKey();
            }
        } catch (IOException | KeyStoreException | UnrecoverableEntryException | CertificateException |
                 NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }

        return privateKey;
    }

    private PublicKey loadPublicKeyFromJKS(String jksPath, String password, String alias) {
        PublicKey publicKey = null;

        try (InputStream is = JwtConfig.class.getResourceAsStream(jksPath)) {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(is, password.toCharArray());

            Certificate cert = keyStore.getCertificate(alias);
            if (cert != null) {
                publicKey = cert.getPublicKey();
            }
        } catch (IOException | KeyStoreException | CertificateException |
                 NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }

        return publicKey;
    }

}

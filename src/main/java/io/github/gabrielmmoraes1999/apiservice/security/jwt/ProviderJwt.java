package io.github.gabrielmmoraes1999.apiservice.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.Instant;
import java.util.Date;

/**
 * JWT helper. HMAC usage requires an explicit secret via {@link #ProviderJwt(String)}
 * or {@link #ProviderJwt(String, String)}. Subclasses may use the protected no-arg
 * constructor and override {@link #generateToken} / {@link #checkToken}.
 */
public class ProviderJwt {

    private static final String DEFAULT_ISSUER = "your-api";

    private final Algorithm algorithm;
    private final String issuer;

    /**
     * For subclasses that override token generation/verification entirely.
     */
    protected ProviderJwt() {
        this.algorithm = null;
        this.issuer = DEFAULT_ISSUER;
    }

    public ProviderJwt(String secret) {
        this(secret, DEFAULT_ISSUER);
    }

    public ProviderJwt(String secret, String issuer) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("JWT secret must be configured explicitly");
        }
        this.algorithm = Algorithm.HMAC256(secret);
        this.issuer = (issuer == null || issuer.isBlank()) ? DEFAULT_ISSUER : issuer;
    }

    public String generateToken(String subject, long expiresIn) {
        requireAlgorithm();
        Instant now = Instant.now();
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(subject)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(expiresIn)))
                .sign(algorithm);
    }

    public String checkToken(String token) {
        requireAlgorithm();
        try {
            DecodedJWT jwt = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token);
            return jwt.getSubject();
        } catch (JWTVerificationException ex) {
            return null;
        }
    }

    protected String getIssuer() {
        return issuer;
    }

    private void requireAlgorithm() {
        if (algorithm == null) {
            throw new IllegalStateException(
                    "ProviderJwt is not configured. Register a @Component extending ProviderJwt "
                            + "or construct ProviderJwt with an explicit secret.");
        }
    }

}

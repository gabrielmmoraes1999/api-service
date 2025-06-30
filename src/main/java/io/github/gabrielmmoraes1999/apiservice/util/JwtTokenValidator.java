package io.github.gabrielmmoraes1999.apiservice.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.Instant;
import java.util.Date;

public class JwtTokenValidator {

    private static final String ISSUER = "your-api";
    private static final String SECRET = "secreta-chave-jwt";

    public static String generateJwtToken(String username, int expiresIn) {
        Instant now = Instant.now();
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(username)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(expiresIn)))
                .sign(Algorithm.HMAC256(SECRET));
    }

    public static String validateToken(String token) {
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(SECRET))
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token);
            return jwt.getSubject();
        } catch (JWTVerificationException ex) {
            return null;
        }
    }
}


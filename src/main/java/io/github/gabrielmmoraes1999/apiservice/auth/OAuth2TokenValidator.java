package io.github.gabrielmmoraes1999.apiservice.auth;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.net.URL;
import java.util.Date;

public class OAuth2TokenValidator {

    private final String issuer;
    private final String audience;

    public OAuth2TokenValidator(String issuer, String audience) {
        this.issuer = issuer;
        this.audience = audience;
    }

    public boolean validate(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSHeader header = signedJWT.getHeader();
            JWKSet jwkSet = JWKSet.load(new URL(issuer + "/.well-known/jwks.json").openStream());
            JWK jwk = jwkSet.getKeyByKeyId(header.getKeyID());

            JWSVerifier verifier = new RSASSAVerifier((RSAKey) jwk);
            if (!signedJWT.verify(verifier)) {
                return false;
            }

            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            return claims.getIssuer().equals(issuer) &&
                    claims.getAudience().contains(audience) &&
                    new Date().before(claims.getExpirationTime());
        } catch (Exception e) {
            return false;
        }
    }

}


package io.github.gabrielmmoraes1999.apiservice.security.jwt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProviderJwtTest {

    @Test
    void requiresExplicitSecret() {
        assertThrows(IllegalArgumentException.class, () -> new ProviderJwt(null));
        assertThrows(IllegalArgumentException.class, () -> new ProviderJwt(""));
        assertThrows(IllegalArgumentException.class, () -> new ProviderJwt("   "));
    }

    @Test
    void subclassWithoutOverrideFailsWhenUsingDefaultMethods() {
        ProviderJwt unconfigured = new ProviderJwt() {};
        assertThrows(IllegalStateException.class, () -> unconfigured.generateToken("user", 60));
        assertThrows(IllegalStateException.class, () -> unconfigured.checkToken("token"));
    }

    @Test
    void generateAndVerifyRoundTrip() {
        ProviderJwt jwt = new ProviderJwt("unit-test-secret-key-32chars!!", "test-issuer");
        String token = jwt.generateToken("subject-1", 120);
        assertNotNull(token);
        assertEquals("subject-1", jwt.checkToken(token));
    }

    @Test
    void invalidTokenReturnsNull() {
        ProviderJwt jwt = new ProviderJwt("unit-test-secret-key-32chars!!");
        assertNull(jwt.checkToken("not-a-jwt"));
    }

}

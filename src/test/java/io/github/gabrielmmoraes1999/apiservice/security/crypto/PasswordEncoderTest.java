package io.github.gabrielmmoraes1999.apiservice.security.crypto;

import io.github.gabrielmmoraes1999.apiservice.security.crypto.bcrypt.BCryptPasswordEncoder;
import io.github.gabrielmmoraes1999.apiservice.security.crypto.md5.Md5PasswordEncoder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderTest {

    @Test
    void bcryptMatchesEncodedSecret() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode("client-secret");
        assertTrue(encoder.matches("client-secret", encoded));
        assertFalse(encoder.matches("wrong", encoded));
    }

    @Test
    @SuppressWarnings("deprecation")
    void md5MatchesIgnoreCase() {
        PasswordEncoder encoder = new Md5PasswordEncoder();
        String encoded = encoder.encode("secret");
        assertTrue(encoder.matches("secret", encoded.toUpperCase()));
        assertFalse(encoder.matches("other", encoded));
    }

    @Test
    void constantTimeEquals() {
        assertTrue(CryptoUtils.constantTimeEquals("abc", "abc"));
        assertFalse(CryptoUtils.constantTimeEquals("abc", "abd"));
        assertFalse(CryptoUtils.constantTimeEquals(null, "abc"));
        assertFalse(CryptoUtils.constantTimeEquals("abc", null));
    }

}

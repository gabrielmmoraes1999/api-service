package io.github.gabrielmmoraes1999.apiservice.security.oauth2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpaqueTokenGeneratorTest {

    @Test
    void generatesOpaqueTokensOfExpectedEncoding() {
        String base64 = OpaqueTokenGenerator.generate(32, OpaqueTokenEncoding.BASE64_URL);
        assertFalse(base64.isBlank());
        assertFalse(base64.contains("+"));
        assertFalse(base64.contains("/"));

        String hex = OpaqueTokenGenerator.generate(16, OpaqueTokenEncoding.HEX);
        assertEquals(32, hex.length());

        String base62 = OpaqueTokenGenerator.generate(16, OpaqueTokenEncoding.BASE62);
        assertFalse(base62.isBlank());
    }

    @Test
    void rejectsInvalidSize() {
        assertThrows(IllegalArgumentException.class,
                () -> OpaqueTokenGenerator.generate(0, OpaqueTokenEncoding.HEX));
    }

}

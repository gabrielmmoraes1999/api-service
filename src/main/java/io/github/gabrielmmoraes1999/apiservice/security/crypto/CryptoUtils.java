package io.github.gabrielmmoraes1999.apiservice.security.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class CryptoUtils {

    private CryptoUtils() {
    }

    /**
     * Constant-time comparison for secrets (UTF-8). Returns false if either value is null.
     */
    public static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(aBytes, bBytes);
    }

}

package io.github.gabrielmmoraes1999.apiservice.security.oauth2;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

public final class OpaqueTokenGenerator {

    private static final String BASE62_ALPHABET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final BigInteger BASE62_RADIX = BigInteger.valueOf(BASE62_ALPHABET.length());
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private OpaqueTokenGenerator() {
    }

    public static String generate(int sizeBytes, OpaqueTokenEncoding encoding) {
        if (sizeBytes <= 0) {
            throw new IllegalArgumentException("Opaque token size must be greater than zero");
        }

        byte[] randomBytes = new byte[sizeBytes];
        SECURE_RANDOM.nextBytes(randomBytes);

        return switch (encoding) {
            case BASE64_URL -> Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
            case HEX -> HexFormat.of().formatHex(randomBytes);
            case BASE62 -> encodeBase62(randomBytes);
        };
    }

    private static String encodeBase62(byte[] bytes) {
        int leadingZeroBytes = 0;
        while (leadingZeroBytes < bytes.length && bytes[leadingZeroBytes] == 0) {
            leadingZeroBytes++;
        }

        BigInteger value = new BigInteger(1, bytes);
        StringBuilder encoded = new StringBuilder();

        while (value.signum() > 0) {
            BigInteger[] division = value.divideAndRemainder(BASE62_RADIX);
            encoded.append(BASE62_ALPHABET.charAt(division[1].intValue()));
            value = division[0];
        }

        encoded.reverse();
        return BASE62_ALPHABET.substring(0, 1).repeat(leadingZeroBytes) + encoded;
    }
}

package io.github.gabrielmmoraes1999.apiservice.security.crypto.md5;

import io.github.gabrielmmoraes1999.apiservice.security.crypto.CryptoUtils;
import io.github.gabrielmmoraes1999.apiservice.security.crypto.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @deprecated MD5 is not suitable for password/secret hashing. Prefer
 * {@link io.github.gabrielmmoraes1999.apiservice.security.crypto.bcrypt.BCryptPasswordEncoder}.
 */
@Deprecated
public class Md5PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(rawPassword.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash MD5", e);
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null) {
            return false;
        }
        return CryptoUtils.constantTimeEquals(
                encode(rawPassword).toLowerCase(),
                encodedPassword.toLowerCase()
        );
    }

}

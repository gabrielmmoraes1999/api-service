package io.github.gabrielmmoraes1999.apiservice.security.oauth2;

public enum RefreshTokenFormat {
    UUID,
    JWT,
    OPAQUE_32_BASE64_URL(32, OpaqueTokenEncoding.BASE64_URL),
    OPAQUE_32_HEX(32, OpaqueTokenEncoding.HEX),
    OPAQUE_32_BASE62(32, OpaqueTokenEncoding.BASE62),
    OPAQUE_64_BASE64_URL(64, OpaqueTokenEncoding.BASE64_URL),
    OPAQUE_64_HEX(64, OpaqueTokenEncoding.HEX),
    OPAQUE_64_BASE62(64, OpaqueTokenEncoding.BASE62);

    private final Integer opaqueSizeBytes;
    private final OpaqueTokenEncoding opaqueEncoding;

    RefreshTokenFormat() {
        this(null, null);
    }

    RefreshTokenFormat(Integer opaqueSizeBytes, OpaqueTokenEncoding opaqueEncoding) {
        this.opaqueSizeBytes = opaqueSizeBytes;
        this.opaqueEncoding = opaqueEncoding;
    }

    public boolean isOpaque() {
        return opaqueSizeBytes != null;
    }

    public int getOpaqueSizeBytes() {
        return opaqueSizeBytes;
    }

    public OpaqueTokenEncoding getOpaqueEncoding() {
        return opaqueEncoding;
    }
}

package io.github.gabrielmmoraes1999.apiservice.security;

public class SecurityContext {

    private Authentication authentication;

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

}

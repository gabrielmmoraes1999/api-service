package io.github.gabrielmmoraes1999.apiservice.security.web;

import io.github.gabrielmmoraes1999.apiservice.security.Authentication;
import io.github.gabrielmmoraes1999.apiservice.security.GrantedAuthority;
import io.github.gabrielmmoraes1999.apiservice.security.UserDetails;

import java.util.Collection;

public class UsernamePasswordAuthenticationToken implements Authentication {

    private final UserDetails principal;
    private final Object credentials;
    private final Collection<? extends GrantedAuthority> authorities;

    public UsernamePasswordAuthenticationToken(UserDetails principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        this.principal = principal;
        this.credentials = credentials;
        this.authorities = authorities;
    }

    @Override
    public UserDetails getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

}

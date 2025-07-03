package io.github.gabrielmmoraes1999.apiservice.security;


import java.util.Collection;

public interface Authentication {
    UserDetails getPrincipal();
    Object getCredentials();
    Collection<? extends GrantedAuthority> getAuthorities();
}
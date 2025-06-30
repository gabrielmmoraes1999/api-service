package io.github.gabrielmmoraes1999.service.entity;

import io.github.gabrielmmoraes1999.apiservice.security.GrantedAuthority;
import io.github.gabrielmmoraes1999.apiservice.security.SimpleGrantedAuthority;
import io.github.gabrielmmoraes1999.apiservice.security.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class Login implements UserDetails {

    private String uuid;
    private String email;
    private String password;
    private LoginRole role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

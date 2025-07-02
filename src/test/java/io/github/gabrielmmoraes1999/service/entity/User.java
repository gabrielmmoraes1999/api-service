package io.github.gabrielmmoraes1999.service.entity;

import io.github.gabrielmmoraes1999.apiservice.security.GrantedAuthority;
import io.github.gabrielmmoraes1999.apiservice.security.SimpleGrantedAuthority;
import io.github.gabrielmmoraes1999.apiservice.security.UserDetails;
import io.github.gabrielmmoraes1999.db.annotation.Column;
import io.github.gabrielmmoraes1999.db.annotation.PrimaryKey;
import io.github.gabrielmmoraes1999.db.annotation.Table;
import io.github.gabrielmmoraes1999.service.enu.UserRole;

import java.util.Collection;
import java.util.Collections;

@Table(name = "USER")
public class User implements UserDetails {

    @PrimaryKey
    @Column(name = "UUID")
    private String uuid;

    @Column(name = "NAME")
    private String name;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "ROLE")
    private UserRole role;

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
        return username;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

}

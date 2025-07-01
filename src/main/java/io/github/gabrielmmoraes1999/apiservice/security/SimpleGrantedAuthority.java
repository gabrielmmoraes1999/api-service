package io.github.gabrielmmoraes1999.apiservice.security;

public final class SimpleGrantedAuthority implements GrantedAuthority {

    private static final long serialVersionUID = 570L;
    private final String role;

    public SimpleGrantedAuthority(String role) {
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return this.role;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof SimpleGrantedAuthority && this.role.equals(((SimpleGrantedAuthority) obj).role);
        }
    }

    @Override
    public int hashCode() {
        return this.role.hashCode();
    }

    @Override
    public String toString() {
        return this.role;
    }

}
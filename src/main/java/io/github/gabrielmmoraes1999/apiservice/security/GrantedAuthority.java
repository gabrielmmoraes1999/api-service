package io.github.gabrielmmoraes1999.apiservice.security;

import java.io.Serializable;

public interface GrantedAuthority extends Serializable {
    String getAuthority();
}

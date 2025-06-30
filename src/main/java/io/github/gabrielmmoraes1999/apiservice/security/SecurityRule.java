package io.github.gabrielmmoraes1999.apiservice.security;

import java.util.List;

public class SecurityRule {
    public enum AccessType { PERMIT_ALL, AUTHENTICATED, HAS_ROLE }

    private final String pathPattern;
    private final String method;
    private final AccessType accessType;
    private final List<String> roles;

    public SecurityRule(String method, String pathPattern, AccessType accessType, List<String> roles) {
        this.method = method;
        this.pathPattern = pathPattern;
        this.accessType = accessType;
        this.roles = roles;
    }

    public boolean matches(String requestPath, String requestMethod) {
        return requestMethod.equalsIgnoreCase(method) && requestPath.matches(pathPattern.replace("**", ".*"));
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public List<String> getRoles() {
        return roles;
    }
}


package io.github.gabrielmmoraes1999.apiservice.security;

import io.github.gabrielmmoraes1999.apiservice.AntPatterns;

import java.util.List;
import java.util.regex.Pattern;

public class SecurityRule {

    public enum AccessType { PERMIT_ALL, AUTHENTICATED, HAS_ROLE }

    private final Pattern compiledPattern;
    private final String method;
    private final AccessType accessType;
    private final List<String> roles;

    public SecurityRule(String method, String pathPattern, AccessType accessType, List<String> roles) {
        this.method = method;
        this.compiledPattern = AntPatterns.compileSecurity(pathPattern);
        this.accessType = accessType;
        this.roles = roles;
    }

    public boolean matches(String requestPath, String requestMethod) {
        return requestMethod.equalsIgnoreCase(method) && compiledPattern.matcher(requestPath).matches();
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public List<String> getRoles() {
        return roles;
    }

}


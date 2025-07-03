package io.github.gabrielmmoraes1999.apiservice.security.web;

import io.github.gabrielmmoraes1999.apiservice.http.HttpMethod;
import io.github.gabrielmmoraes1999.apiservice.security.SecurityRule;

import java.util.Arrays;

public class PathMatcher {

    private final String pattern;
    private final HttpMethod method;
    private final AuthorizationBuilder builder;

    public PathMatcher(String pattern, HttpMethod method, AuthorizationBuilder builder) {
        this.pattern = pattern;
        this.method = method;
        this.builder = builder;
    }

    public AuthorizationBuilder permitAll() {
        if (method == null) {
            builder.addRule(new SecurityRule("*", pattern, SecurityRule.AccessType.PERMIT_ALL, null));
        } else {
            builder.addRule(new SecurityRule(method.name(), pattern, SecurityRule.AccessType.PERMIT_ALL, null));
        }
        return builder;
    }

    public AuthorizationBuilder hasAnyRole(String... roles) {
        if (method == null) {
            builder.addRule(new SecurityRule("*", pattern, SecurityRule.AccessType.HAS_ROLE, Arrays.asList(roles)));
        } else {
            builder.addRule(new SecurityRule(method.name(), pattern, SecurityRule.AccessType.HAS_ROLE, Arrays.asList(roles)));
        }
        return builder;
    }

}


package io.github.gabrielmmoraes1999.apiservice.security.web;

import io.github.gabrielmmoraes1999.apiservice.http.HttpMethod;
import io.github.gabrielmmoraes1999.apiservice.security.SecurityRule;

import java.util.ArrayList;
import java.util.List;

public class AuthorizationBuilder {

    private final List<SecurityRule> rules = new ArrayList<>();
    private boolean anyRequest;

    public PathMatcher antMatchers(String pattern) {
        return new PathMatcher(pattern, null, this);
    }

    public PathMatcher antMatchers(HttpMethod method, String pattern) {
        return new PathMatcher(pattern, method, this);
    }

    public AuthorizationBuilder anyRequest() {
        this.anyRequest = true;
        return this;
    }

    public AuthorizationBuilder authenticated() {
        if (anyRequest) {
            addRule(new SecurityRule("*", "/**", SecurityRule.AccessType.AUTHENTICATED, List.of()));
            anyRequest = false;
        }
        return this;
    }

    void addRule(SecurityRule rule) {
        rules.add(rule);
    }

    public List<SecurityRule> getRules() {
        return rules;
    }

}

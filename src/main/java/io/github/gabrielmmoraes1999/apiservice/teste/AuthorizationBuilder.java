package io.github.gabrielmmoraes1999.apiservice.teste;

import io.github.gabrielmmoraes1999.apiservice.http.HttpMethod;
import io.github.gabrielmmoraes1999.apiservice.security.SecurityRule;

import java.util.ArrayList;
import java.util.List;

public class AuthorizationBuilder {

    private final List<SecurityRule> rules = new ArrayList<>();

    public PathMatcher antMatchers(String pattern) {
        return new PathMatcher(pattern, null, this);
    }

    public PathMatcher antMatchers(HttpMethod method, String pattern) {
        return new PathMatcher(pattern, method, this);
    }

    public AuthorizationBuilder anyRequest() {
        return this;
    }

    public AuthorizationBuilder authenticated() {
        return this;
    }

    void addRule(SecurityRule rule) {
        rules.add(rule);
    }

    public List<SecurityRule> getRules() {
        return rules;
    }
}

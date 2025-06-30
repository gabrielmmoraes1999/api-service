package io.github.gabrielmmoraes1999.apiservice.teste;

import io.github.gabrielmmoraes1999.apiservice.security.SecurityRule;

import java.util.List;
import java.util.Optional;

public class SecurityFilterChain {

    private static List<SecurityRule> rules;

    public SecurityFilterChain(List<SecurityRule> rules) {
        SecurityFilterChain.rules = rules;
    }

    public static Optional<SecurityRule> getRule(String path, String method) {
        return rules.stream()
                .filter(rule -> rule.matches(path, method) || rule.matches(path, "*"))
                .findFirst();
    }

    public static List<SecurityRule> getRules() {
        return rules;
    }

}

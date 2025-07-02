package io.github.gabrielmmoraes1999.apiservice.security.web;

import io.github.gabrielmmoraes1999.apiservice.security.SecurityRule;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class SecurityFilterChain {

    private static List<SecurityRule> rules;

    public SecurityFilterChain(List<SecurityRule> rules) {
        SecurityFilterChain.rules = rules;
    }

    public static List<SecurityRule> getRule(String path, String method) {
        return rules.stream()
                .filter(rule -> rule.matches(path, method) || rule.matches(path, "*"))
                .collect(Collectors.toList());
    }

    public static List<SecurityRule> getListRule(String path, String method, SecurityRule.AccessType accessType) {
        return rules.stream()
                .filter(rule -> (rule.matches(path, method) || rule.matches(path, "*")) && Objects.equals(rule.getAccessType(), accessType))
                .collect(Collectors.toList());
    }

    public static Optional<SecurityRule> getRule(String path, String method, SecurityRule.AccessType accessType) {
        return rules.stream()
                .filter(rule -> (rule.matches(path, method) || rule.matches(path, "*")) && Objects.equals(rule.getAccessType(), accessType))
                .findFirst();
    }

    public static Optional<SecurityRule> getRule(String path, String method, SecurityRule.AccessType accessType, String nameRole) {
        return rules.stream()
                .filter(rule ->(rule.matches(path, method)|| rule.matches(path, "*"))
                        && Objects.equals(rule.getAccessType(), accessType)
                        && rule.getRoles().contains(nameRole))
                .findFirst();
    }

    public static List<SecurityRule> getRules() {
        return rules;
    }

}

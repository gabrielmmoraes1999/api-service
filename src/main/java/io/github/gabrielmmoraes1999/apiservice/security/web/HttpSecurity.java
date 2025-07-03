package io.github.gabrielmmoraes1999.apiservice.security.web;

import io.github.gabrielmmoraes1999.apiservice.security.SecurityRule;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HttpSecurity {

    private final List<SecurityRule> rules = new ArrayList<>();

    public HttpSecurity authorizeHttpRequests(Consumer<AuthorizationBuilder> consumer) {
        AuthorizationBuilder builder = new AuthorizationBuilder();
        consumer.accept(builder);
        rules.addAll(builder.getRules());
        return this;
    }

    public SecurityFilterChain build() {
        return new SecurityFilterChain(rules);
    }

}


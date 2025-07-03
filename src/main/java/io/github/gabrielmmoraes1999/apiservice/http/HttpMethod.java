package io.github.gabrielmmoraes1999.apiservice.http;

import java.util.HashMap;
import java.util.Map;

public enum HttpMethod {
    GET,
    HEAD,
    POST,
    PUT,
    PATCH,
    DELETE,
    OPTIONS,
    TRACE;

    private static final Map<String, HttpMethod> mappings = new HashMap<>(16);

    public static HttpMethod resolve(String method) {
        return method != null ? (HttpMethod)mappings.get(method) : null;
    }

    public boolean matches(String method) {
        return this.name().equals(method);
    }

    static {
        for(HttpMethod httpMethod : values()) {
            mappings.put(httpMethod.name(), httpMethod);
        }
    }

}

package io.github.gabrielmmoraes1999.apiservice.http;

import java.util.HashMap;
import java.util.Map;

public class ResponseEntity<T> {

    private final T body;
    private final HttpStatus status;
    private final Map<String, String> headers = new HashMap<>();

    public ResponseEntity(T body, HttpStatus status) {
        this.status = status;
        this.body = body;
    }

    public static <T> ResponseEntity<T> ok(T body) {
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    public static <T> ResponseEntity<T> status(HttpStatus status, T body) {
        return new ResponseEntity<>(body, status);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public T getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public ResponseEntity<T> header(String name, String value) {
        headers.put(name, value);
        return this;
    }
}

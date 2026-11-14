package io.github.gabrielmmoraes1999.apiservice.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionUtilsTest {

    @Test
    void toJsonDoesNotExposeStackTrace() {
        String json = ExceptionUtils.toJson(new RuntimeException("boom"));
        assertTrue(json.contains("boom"));
        assertTrue(json.contains("\"error\""));
        assertTrue(json.contains("\"message\""));
        assertTrue(json.contains("\"timestamp\""));
        assertFalse(json.contains("stackTrace"));
        assertFalse(json.contains("at io.github"));
    }

}

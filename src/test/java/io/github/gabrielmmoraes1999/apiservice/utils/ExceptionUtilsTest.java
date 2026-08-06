package io.github.gabrielmmoraes1999.apiservice.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionUtilsTest {

    @Test
    void toJsonDoesNotExposeStackTrace() {
        Logger logger = (Logger) LoggerFactory.getLogger(ExceptionUtils.class);
        Level previous = logger.getLevel();
        logger.setLevel(Level.OFF);
        try {
            String json = ExceptionUtils.toJson(new RuntimeException("boom"));
            assertTrue(json.contains("boom"));
            assertTrue(json.contains("\"error\""));
            assertTrue(json.contains("\"message\""));
            assertTrue(json.contains("\"timestamp\""));
            assertFalse(json.contains("stackTrace"));
            assertFalse(json.contains("at io.github"));
        } finally {
            logger.setLevel(previous);
        }
    }

}

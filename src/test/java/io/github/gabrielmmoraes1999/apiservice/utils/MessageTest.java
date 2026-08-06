package io.github.gabrielmmoraes1999.apiservice.utils;

import io.github.gabrielmmoraes1999.apiservice.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void errorEnvelopeContainsExpectedFields() {
        JSONObject error = Message.error("something went wrong");
        assertEquals("something went wrong", error.getString("error"));
        assertEquals("something went wrong", error.getString("message"));
        assertTrue(error.has("timestamp"));
        assertFalse(error.has("stackTrace"));
    }

}

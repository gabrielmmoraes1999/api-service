package io.github.gabrielmmoraes1999.apiservice.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class ExceptionUtils {

    private static final Logger log = LoggerFactory.getLogger(ExceptionUtils.class);

    public static String toJson(Exception ex) {
        log.error("Unhandled exception", ex);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return mapper.writeValueAsString(new ExceptionResponse(ex));
        } catch (Exception e) {
            return Message.error("Failed to serialize exception").toString();
        }
    }

    public static class ExceptionResponse {
        public String error;
        public String message;
        public String timestamp;

        public ExceptionResponse(Exception ex) {
            String msg = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
            this.error = msg;
            this.message = msg;
            this.timestamp = Instant.now().toString();
        }
    }
}

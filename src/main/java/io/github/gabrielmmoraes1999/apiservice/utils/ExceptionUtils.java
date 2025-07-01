package io.github.gabrielmmoraes1999.apiservice.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExceptionUtils {

    public static String toJson(Exception ex) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return mapper.writeValueAsString(new ExceptionResponse(ex));
        } catch (Exception e) {
            return Message.error("Failed to serialize exception");
        }
    }

    public static class ExceptionResponse {
        public String message;
        public String exception;
        public List<String> stackTrace;
        public Date timestamp;

        public ExceptionResponse(Exception ex) {
            this.message = ex.getMessage();
            this.exception = ex.getClass().getName();
            this.stackTrace = extractStackTrace(ex);
            this.timestamp = new Date();
        }

        private List<String> extractStackTrace(Throwable ex) {
            List<String> result = new ArrayList<>();
            while (ex != null) {
                result.add("Caused by: " + ex.toString());
                for (StackTraceElement element : ex.getStackTrace()) {
                    result.add("    at " + element.toString());
                }
                ex = ex.getCause();
            }
            return result;
        }
    }
}
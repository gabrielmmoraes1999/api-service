package io.github.gabrielmmoraes1999.apiservice.utils;

import io.github.gabrielmmoraes1999.apiservice.json.JSONObject;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

public class Message {

    public static void error(HttpServletResponse response, int codeHttp, String message) throws IOException {
        response.setStatus(codeHttp);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(Message.error(message).toString());
    }

    public static void error(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(Message.error(message).toString());
    }

    public static JSONObject error(String message) {
        JSONObject json = new JSONObject();
        json.put("error", message);
        json.put("message", message);
        json.put("timestamp", Instant.now().toString());
        return json;
    }

}

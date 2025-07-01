package io.github.gabrielmmoraes1999.apiservice.utils;

import io.github.gabrielmmoraes1999.apiservice.json.JSONObject;

import java.util.Collections;

public class Message {

    public static String error(String message) {
        return new JSONObject(Collections.singletonMap("error", message)).toString();
    }

}

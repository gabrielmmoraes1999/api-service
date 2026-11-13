package io.github.gabrielmmoraes1999.apiservice;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RouteInfoTest {

    @Test
    void literalDotIsNotARegexOperator() throws Exception {
        RouteInfo info = route("/api/v1.0/admin");

        assertTrue(info.getPathPattern().matcher("/api/v1.0/admin").matches());
        assertFalse(info.getPathPattern().matcher("/api/v1X0/admin").matches());
        assertFalse(info.getPathPattern().matcher("/api/v1.0/admin/").matches());
    }

    @Test
    void starStaysInsideOneSegment() throws Exception {
        RouteInfo info = route("/files/*.json");

        assertTrue(info.getPathPattern().matcher("/files/a.json").matches());
        assertFalse(info.getPathPattern().matcher("/files/a/b.json").matches());
        assertFalse(info.getPathPattern().matcher("/filesXjson").matches());
    }

    @Test
    void doubleStarMatchesChildAndNotADotWildcard() throws Exception {
        RouteInfo info = route("/api/v1.0/**");

        assertTrue(info.getPathPattern().matcher("/api/v1.0/admin").matches());
        assertTrue(info.getPathPattern().matcher("/api/v1.0").matches());
        assertFalse(info.getPathPattern().matcher("/api/v1X0/admin").matches());
    }

    @Test
    void pathVariableStillCaptured() throws Exception {
        RouteInfo info = route("/client/{id}");
        Matcher matcher = info.getPathPattern().matcher("/client/abc");

        assertTrue(matcher.matches());
        assertEquals("abc", matcher.group(1));
        assertEquals(List.of("id"), info.getPathVariableNames());
        assertFalse(info.getPathPattern().matcher("/client/a/b").matches());
    }

    @Test
    void severalPathVariablesKeepOrderAroundLiteralDot() throws Exception {
        RouteInfo info = route("/api/v1.0/{id}/item/{sku}");
        Matcher matcher = info.getPathPattern().matcher("/api/v1.0/abc/item/42");

        assertTrue(matcher.matches());
        assertEquals("abc", matcher.group(1));
        assertEquals("42", matcher.group(2));
        assertEquals(List.of("id", "sku"), info.getPathVariableNames());
        assertFalse(info.getPathPattern().matcher("/api/v1X0/abc/item/42").matches());
    }

    private static RouteInfo route(String path) throws Exception {
        Method method = RouteInfoTest.class.getDeclaredMethod("sample");
        return RouteInfo.createRouteInfo(method, null, path);
    }

    static void sample() {
    }

}

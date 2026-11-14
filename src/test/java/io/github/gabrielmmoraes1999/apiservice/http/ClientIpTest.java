package io.github.gabrielmmoraes1999.apiservice.http;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientIpTest {

    @Test
    void remoteAddressOnlyIgnoresProxyHeaders() {
        HttpServletRequest request = request("10.0.0.8", Map.of(
                "X-Forwarded-For", "203.0.113.10, 10.0.0.1",
                "X-Real-IP", "203.0.113.10"
        ), true);

        assertEquals("10.0.0.8", ClientIp.remoteAddressOnly().resolve(request));
    }

    @Test
    void trustProxyHeadersUsesFirstForwardedHop() {
        AtomicInteger realIpReads = new AtomicInteger();
        HttpServletRequest request = request("10.0.0.8", Map.of(
                "X-Forwarded-For", " 203.0.113.10 , 10.0.0.1"
        ), false, realIpReads);

        assertEquals("203.0.113.10", ClientIp.trustProxyHeaders().resolve(request));
        assertEquals(0, realIpReads.get());
    }

    @Test
    void trustProxyHeadersFallsBackToRealIpThenRemoteAddress() {
        HttpServletRequest realIp = request("10.0.0.8", Map.of("X-Real-IP", " 203.0.113.20 "), false);
        assertEquals("203.0.113.20", ClientIp.trustProxyHeaders().resolve(realIp));

        HttpServletRequest blankHeaders = request("10.0.0.8", Map.of(
                "X-Forwarded-For", "  ",
                "X-Real-IP", ""
        ), false);
        assertEquals("10.0.0.8", ClientIp.trustProxyHeaders().resolve(blankHeaders));
    }

    @Test
    void nullRequestReturnsNull() {
        assertNull(ClientIp.remoteAddressOnly().resolve(null));
        assertNull(ClientIp.trustProxyHeaders().resolve(null));
    }

    private static HttpServletRequest request(String remoteAddress, Map<String, String> headers, boolean forbidHeaders) {
        return request(remoteAddress, headers, forbidHeaders, new AtomicInteger());
    }

    private static HttpServletRequest request(
            String remoteAddress,
            Map<String, String> headers,
            boolean forbidHeaders,
            AtomicInteger realIpReads
    ) {
        Map<String, String> values = new HashMap<>(headers);
        return (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getRemoteAddr" -> remoteAddress;
                    case "getHeader" -> {
                        String name = (String) args[0];
                        if (forbidHeaders) {
                            throw new AssertionError("header read: " + name);
                        }
                        if ("X-Real-IP".equals(name)) {
                            realIpReads.incrementAndGet();
                        }
                        yield values.get(name);
                    }
                    case "toString" -> "request";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> defaultValue(method.getReturnType());
                });
    }

    private static Object defaultValue(Class<?> type) {
        if (type == boolean.class) {
            return false;
        }
        if (type == int.class) {
            return 0;
        }
        if (type == long.class) {
            return 0L;
        }
        return null;
    }

}

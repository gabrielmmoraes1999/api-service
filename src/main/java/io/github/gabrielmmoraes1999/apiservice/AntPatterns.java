package io.github.gabrielmmoraes1999.apiservice;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Compiles Ant-style paths. Literal text is quoted, so {@code .} and other
 * regex metacharacters stay literal. {@code **} is zero or more segments,
 * {@code *} stays inside one segment, and {@code ?} is one character other than {@code /}.
 */
public final class AntPatterns {

    private AntPatterns() {
    }

    public static Pattern compileSecurity(String pattern) {
        return compile(pattern, false, true, new ArrayList<>());
    }

    public static Pattern compileRoute(String fullPath, List<String> variableNames) {
        return compile(fullPath, true, false, variableNames);
    }

    private static Pattern compile(String pattern, boolean captureVariables, boolean optionalTrailingSlash,
                                   List<String> variableNames) {
        StringBuilder regex = new StringBuilder("^");
        int i = 0;
        int length = pattern.length();
        while (i < length) {
            if (pattern.charAt(i) == '/' && pattern.startsWith("**", i + 1)) {
                i++;
                continue;
            }
            if (pattern.startsWith("**", i)) {
                regex.append("(?:/[^/]*)*");
                i += 2;
                continue;
            }
            char c = pattern.charAt(i);
            if (c == '*') {
                regex.append("[^/]*");
                i++;
                continue;
            }
            if (c == '?') {
                regex.append("[^/]");
                i++;
                continue;
            }
            if (captureVariables && isVariable(pattern, i)) {
                int end = pattern.indexOf('}', i + 1);
                variableNames.add(pattern.substring(i + 1, end));
                regex.append("([^/]+)");
                i = end + 1;
                continue;
            }
            int start = i;
            i = nextLiteralEnd(pattern, i, captureVariables);
            if (i == start) {
                throw new IllegalArgumentException("Unsupported path pattern: " + pattern);
            }
            regex.append(Pattern.quote(pattern.substring(start, i)));
        }
        if (optionalTrailingSlash) {
            regex.append("/?");
        }
        regex.append('$');
        return Pattern.compile(regex.toString());
    }

    private static int nextLiteralEnd(String pattern, int i, boolean captureVariables) {
        int length = pattern.length();
        while (i < length) {
            if (pattern.startsWith("**", i) || pattern.charAt(i) == '*' || pattern.charAt(i) == '?') {
                break;
            }
            if (captureVariables && isVariable(pattern, i)) {
                break;
            }
            if (pattern.charAt(i) == '/' && pattern.startsWith("**", i + 1)) {
                break;
            }
            i++;
        }
        return i;
    }

    private static boolean isVariable(String pattern, int i) {
        if (pattern.charAt(i) != '{') {
            return false;
        }
        int end = pattern.indexOf('}', i + 1);
        if (end <= i + 1) {
            return false;
        }
        for (int index = i + 1; index < end; index++) {
            if (pattern.charAt(index) == '/') {
                return false;
            }
        }
        return true;
    }

}

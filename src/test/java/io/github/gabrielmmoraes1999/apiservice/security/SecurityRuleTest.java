package io.github.gabrielmmoraes1999.apiservice.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityRuleTest {

    @Test
    void doubleStarDoesNotTreatDotAsRegex() {
        SecurityRule rule = rule("/api/v1.0/**");

        assertTrue(rule.matches("/api/v1.0/admin", "GET"));
        assertTrue(rule.matches("/api/v1.0/admin/", "GET"));
        assertFalse(rule.matches("/api/v1X0/admin", "GET"));
    }

    @Test
    void singleStarStaysInsideOneSegment() {
        SecurityRule rule = rule("/files/*.json");

        assertTrue(rule.matches("/files/a.json", "GET"));
        assertFalse(rule.matches("/files/a/b.json", "GET"));
        assertFalse(rule.matches("/filesXjson", "GET"));
    }

    @Test
    void questionMarkMatchesOneCharacterInsideSegment() {
        SecurityRule rule = rule("/files/a?c");

        assertTrue(rule.matches("/files/abc", "GET"));
        assertFalse(rule.matches("/files/abxc", "GET"));
        assertFalse(rule.matches("/files/a/c", "GET"));
    }

    @Test
    void userPatternMatchesWithAndWithoutTrailingSlash() {
        SecurityRule exact = rule("/api/user");
        SecurityRule nested = rule("/api/user/**");

        assertTrue(exact.matches("/api/user", "GET"));
        assertTrue(exact.matches("/api/user/", "GET"));
        assertFalse(exact.matches("/api/user/admin", "GET"));

        assertTrue(nested.matches("/api/user", "GET"));
        assertTrue(nested.matches("/api/user/", "GET"));
        assertTrue(nested.matches("/api/user/1/", "GET"));
    }

    @Test
    void methodStillHasToMatch() {
        SecurityRule rule = new SecurityRule("GET", "/files/*.json", SecurityRule.AccessType.PERMIT_ALL, null);

        assertTrue(rule.matches("/files/a.json", "get"));
        assertFalse(rule.matches("/files/a.json", "POST"));
    }

    private static SecurityRule rule(String pattern) {
        return new SecurityRule("GET", pattern, SecurityRule.AccessType.PERMIT_ALL, null);
    }

}

package com.project.groceryTracker.Security;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    @Test
    void generatesAndValidatesTokenForItsSubject() {
        JwtService jwtService = new JwtService(
                Base64.getEncoder().encodeToString(new byte[32]),
                60_000
        );

        String token = jwtService.generateToken("user@example.com");

        assertEquals("user@example.com", jwtService.extractEmail(token));
        assertTrue(jwtService.isTokenValid(token, "USER@example.com"));
        assertFalse(jwtService.isTokenValid(token, "other@example.com"));
    }
}

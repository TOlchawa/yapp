package com.memoritta.server.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void testGenerateAndExtractUsername() {
        String username = "testUser";
        String token = jwtUtil.generateToken(username);

        String extractedUsername = jwtUtil.extractUsername(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void testValidateTokenSuccess() {
        String username = "validUser";
        String token = jwtUtil.generateToken(username);

        assertTrue(jwtUtil.validateToken(token, username));
    }

    @Test
    void testValidateTokenWithWrongUsername() {
        String username = "userA";
        String token = jwtUtil.generateToken(username);

        assertFalse(jwtUtil.validateToken(token, "userB"));
    }

    @Test
    void testIsTokenExpired_falseForNewToken() {
        String token = jwtUtil.generateToken("newUser");

        assertFalse(isExpired(token));
    }

    // Helper to expose private method indirectly
    private boolean isExpired(String token) {
        return !jwtUtil.validateToken(token, jwtUtil.extractUsername(token));
    }

    @Test
    void testTokenExpiration_manuallyExpired() throws InterruptedException {
        // Override token with immediate expiration
        String expiredToken = Jwts.builder()
                .subject("expiredUser")
                .issuedAt(new Date(System.currentTimeMillis() - 2000))
                .expiration(new Date(System.currentTimeMillis() - 1000)) // already expired
                .signWith(
                        Keys.hmacShaKeyFor("Q+FQIAMDM+KevA+A3qnUiHO0eo27QB3tm2ahv3WqDlw=".getBytes()),
                        Jwts.SIG.HS256
                )
                .compact();

        assertThrows(ExpiredJwtException.class, () -> jwtUtil.extractUsername(expiredToken));
    }
}

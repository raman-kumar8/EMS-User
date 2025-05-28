package com.example.emsuser.security;

import com.example.emsuser.exception.CustomException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final UUID userId = UUID.randomUUID();
    private final String role = "USER";
    private final String secretKey = "testSecretKeyForJwtTokenProviderTestingPurposesOnly";

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", secretKey);
    }

    @Test
    void generateToken_CreatesValidToken() {
        // Act
        String token = jwtTokenProvider.generateToken(userId, role);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);

        // Verify token can be parsed
        byte[] decodedKey = Base64.getEncoder().encode(secretKey.getBytes());
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(decodedKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(userId.toString(), claims.getSubject());
        assertEquals(role, claims.get("role", String.class));
    }

    @Test
    void getUserIdFromJWT_ReturnsCorrectUserId() {
        // Arrange
        String token = jwtTokenProvider.generateToken(userId, role);

        // Act
        UUID extractedUserId = jwtTokenProvider.getUserIdFromJWT(token);

        // Assert
        assertEquals(userId, extractedUserId);
    }

    @Test
    void getRoleFromJWT_ReturnsCorrectRole() {
        // Arrange
        String token = jwtTokenProvider.generateToken(userId, role);

        // Act
        String extractedRole = jwtTokenProvider.getRoleFromJWT(token);

        // Assert
        assertEquals(role, extractedRole);
    }

    @Test
    void validateToken_WithValidToken_ReturnsTrue() {
        // Arrange
        String token = jwtTokenProvider.generateToken(userId, role);

        // Act & Assert
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void validateToken_WithInvalidToken_ReturnsFalse() {
        // Arrange
        String invalidToken = "invalid.token.string";

        // Act
        boolean result = jwtTokenProvider.validateToken(invalidToken);

        // Assert
        assertFalse(result);
    }
}

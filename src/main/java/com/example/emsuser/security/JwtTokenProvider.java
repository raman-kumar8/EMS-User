package com.example.emsuser.security;

import com.example.emsuser.exception.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * Provider class for JWT token generation and validation.
 * Handles token creation, parsing, and validation operations.
 */
@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${SecretKey}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms:86400000}")
    private long jwtExpirationInMs; // Default: 1 day

    private Key key = null;

    /**
     * Lazily initializes and returns the signing key used for JWT operations.
     * @return The signing key
     */
    private Key getSigningKey() {
        if (key == null) {
            // Correctly decode the secret key from Base64
            byte[] decodedKey = Base64.getDecoder().decode(jwtSecret.getBytes());
            key = Keys.hmacShaKeyFor(decodedKey);
        }
        return key;
    }

    /**
     * Generates a JWT token for the given user ID and role.
     * @param userId The user's UUID
     * @param role The user's role
     * @return A JWT token string
     */
    public String generateToken(UUID userId, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extracts the claims from a JWT token.
     * @param token The JWT token
     * @return The claims contained in the token
     * @throws CustomException if the token is invalid
     */
    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception ex) {
            throw new CustomException("Invalid JWT token: " + ex.getMessage());
        }
    }

    /**
     * Extracts the user ID from a JWT token.
     * @param token The JWT token
     * @return The user's UUID
     */
    public UUID getUserIdFromJWT(String token) {
        Claims claims = getClaimsFromToken(token);
        return UUID.fromString(claims.getSubject());
    }

    /**
     * Extracts the role from a JWT token.
     * @param token The JWT token
     * @return The user's role
     */
    public String getRoleFromJWT(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    /**
     * Validates a JWT token.
     * @param token The JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException ex) {
            logger.error("Invalid JWT signature", ex);
            return false;
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token", ex);
            return false;
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token", ex);
            return false;
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty", ex);
            return false;
        }
    }
}

package com.example.emsuser.security;

import com.example.emsuser.exception.CustomException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationFilterTest {
    private JwtTokenProvider jwtTokenProvider;
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private final String secretKey = "testSecretKeyForJwtTokenProviderTestingPurposesOnly";
    private final UUID userId = UUID.randomUUID();
    private final String role = "USER";

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", secretKey);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider);
        SecurityContextHolder.clearContext();
    }

    private String generateToken() {
        return jwtTokenProvider.generateToken(userId, role);
    }

    @Test
    void doFilterInternal_validToken_setsAuthentication() throws Exception {
        String token = generateToken();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setCookies(new jakarta.servlet.http.Cookie("jwt_token", token));

        jwtAuthenticationFilter.doFilterInternal(request, response, (req, res) -> {
            assertNotNull(SecurityContextHolder.getContext().getAuthentication());
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            assertEquals(userId.toString(), userDetails.getUsername());
            assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role)));
        });
    }

    @Test
    void doFilterInternal_invalidToken_clearsAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setCookies(new jakarta.servlet.http.Cookie("jwt_token", "invalid.token"));

        jwtAuthenticationFilter.doFilterInternal(request, response, (req, res) -> {
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        });
    }

    @Test
    void doFilterInternal_noToken_doesNotSetAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilterInternal(request, response, (req, res) -> {
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        });
    }
}

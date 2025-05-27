package com.example.emsuser.controller;

import com.example.emsuser.dto.UserDetailsResponseDTo;
import com.example.emsuser.model.UserModel;
import com.example.emsuser.model.UserRoleModel;
import com.example.emsuser.security.JwtTokenProvider;
import com.example.emsuser.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GeneralControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private GeneralController generalController;

    private final String validToken = "valid.jwt.token";
    private final UUID userId = UUID.randomUUID();
    private UserModel mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup mock user
        mockUser = new UserModel();
        mockUser.setId(userId);
        mockUser.setName("Test User");
        mockUser.setEmail("test@example.com");

        // Setup mock role
        UserRoleModel role = new UserRoleModel();
        role.setRole("USER");
        mockUser.setRole(role);

        // Setup mock JWT token provider
        when(jwtTokenProvider.getUserIdFromJWT(validToken)).thenReturn(userId);

        // Setup mock user service
        when(userService.getUserById(userId)).thenReturn(mockUser);
    }

    @Test
    void validate_WithValidToken_ReturnsUserId() {
        // Act
        String result = generalController.validate(validToken);

        // Assert
        assertEquals(userId.toString(), result);
        verify(jwtTokenProvider, times(1)).getUserIdFromJWT(validToken);
    }

    @Test
    void validate_WithInvalidToken_ThrowsRuntimeException() {
        // Arrange
        when(jwtTokenProvider.getUserIdFromJWT("invalid.token")).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> generalController.validate("invalid.token"));
        verify(jwtTokenProvider, times(1)).getUserIdFromJWT("invalid.token");
    }

    @Test
    void getUserDetails_WithValidToken_ReturnsUserDetails() {
        // Act
        ResponseEntity<UserDetailsResponseDTo> response = generalController.getUserDetails(validToken);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserDetailsResponseDTo responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(mockUser.getName(), responseBody.getName());
        assertEquals(mockUser.getEmail(), responseBody.getEmail());
        assertEquals(mockUser.getRole().getRole(), responseBody.getRole());

        verify(jwtTokenProvider, times(1)).getUserIdFromJWT(validToken);
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void getUserDetails_WithInvalidToken_ThrowsRuntimeException() {
        // Arrange
        String invalidToken = "invalid.token";
        when(jwtTokenProvider.getUserIdFromJWT(invalidToken)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            generalController.getUserDetails(invalidToken);
        });

        verify(jwtTokenProvider, times(1)).getUserIdFromJWT(invalidToken);
        verify(userService, never()).getUserById(any(UUID.class));
    }

    @Test
    void getUserDetails_WithValidTokenButUserNotFound_ThrowsRuntimeException() {
        // Arrange
        UUID nonExistentUserId = UUID.randomUUID();
        String tokenForNonExistentUser = "token.for.non.existent.user";
        when(jwtTokenProvider.getUserIdFromJWT(tokenForNonExistentUser)).thenReturn(nonExistentUserId);
        when(userService.getUserById(nonExistentUserId)).thenThrow(new RuntimeException("User not found with id: "));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            generalController.getUserDetails(tokenForNonExistentUser);
        });

        assertEquals("User not found with id: ", exception.getMessage());
        verify(jwtTokenProvider, times(1)).getUserIdFromJWT(tokenForNonExistentUser);
        verify(userService, times(1)).getUserById(nonExistentUserId);
    }
}

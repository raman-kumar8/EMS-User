package com.example.emsuser.controller;

import com.example.emsuser.dto.UpdateDTO;
import com.example.emsuser.dto.UserLoginDTO;
import com.example.emsuser.dto.UserRegisterDTO;
import com.example.emsuser.dto.UserResponseDTO;
import com.example.emsuser.exception.CustomException;
import com.example.emsuser.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private UserController userController;

    private UserRegisterDTO registerDTO;
    private UserLoginDTO loginDTO;
    private UpdateDTO updateDTO;
    private UserResponseDTO responseDTO;
    private final String validToken = "valid.jwt.token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup register DTO
        registerDTO = new UserRegisterDTO();
        registerDTO.setName("Test User");
        registerDTO.setEmail("test@example.com");
        registerDTO.setPassword("password123");
        registerDTO.setRole("USER");

        // Setup login DTO
        loginDTO = new UserLoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("password123");

        // Setup update DTO
        updateDTO = new UpdateDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setEmail("updated@example.com");

        // Setup response DTO
        responseDTO = new UserResponseDTO();
        responseDTO.setName("Test User");
        responseDTO.setEmail("test@example.com");
        responseDTO.setRole("USER");

        // Mock service responses
        ResponseEntity<UserResponseDTO> mockResponse = ResponseEntity.ok(responseDTO);
        when(userService.registerUser(any(UserRegisterDTO.class))).thenReturn(mockResponse);
        when(userService.login(any(UserLoginDTO.class), any(HttpServletResponse.class))).thenReturn(mockResponse);
        when(userService.update(anyString(), any(UpdateDTO.class))).thenReturn(mockResponse);
    }

    @Test
    void registerUser_WithValidData_ReturnsCreatedStatus() {
        // Act
        ResponseEntity<UserResponseDTO> response = userController.registerUser(registerDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(userService, times(1)).registerUser(registerDTO);
    }

    @Test
    void loginUser_WithValidCredentials_ReturnsOkStatus() {
        // Act
        ResponseEntity<UserResponseDTO> result = userController.loginUser(loginDTO, response);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseDTO, result.getBody());
        verify(userService, times(1)).login(loginDTO, response);
    }

    @Test
    void logout_ClearsJwtCookie_ReturnsOkStatus() {
        // Act
        ResponseEntity<String> result = userController.logout(response);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Logged out successfully", result.getBody());
        verify(response, times(1)).addHeader(eq("Set-Cookie"), anyString());
    }

    @Test
    void updateUser_WithValidData_ReturnsOkStatus() {
        // Act
        ResponseEntity<UserResponseDTO> result = userController.updateUser(validToken, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseDTO, result.getBody());
        verify(userService, times(1)).update(validToken, updateDTO);
    }

    @Test
    void registerUser_WithExistingEmail_ThrowsCustomException() {
        // Arrange
        when(userService.registerUser(any(UserRegisterDTO.class)))
                .thenThrow(new CustomException("Email is already in use"));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            userController.registerUser(registerDTO);
        });

        assertEquals("Email is already in use", exception.getMessage());
        verify(userService, times(1)).registerUser(registerDTO);
    }

    @Test
    void loginUser_WithNonExistentUser_ThrowsRuntimeException() {
        // Arrange
        when(userService.login(any(UserLoginDTO.class), any(HttpServletResponse.class)))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userController.loginUser(loginDTO, response);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userService, times(1)).login(loginDTO, response);
    }

    @Test
    void loginUser_WithInvalidPassword_ThrowsRuntimeException() {
        // Arrange
        when(userService.login(any(UserLoginDTO.class), any(HttpServletResponse.class)))
                .thenThrow(new RuntimeException("Invalid password"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userController.loginUser(loginDTO, response);
        });

        assertEquals("Invalid password", exception.getMessage());
        verify(userService, times(1)).login(loginDTO, response);
    }

    @Test
    void updateUser_WithNonExistentUser_ThrowsRuntimeException() {
        // Arrange
        when(userService.update(anyString(), any(UpdateDTO.class)))
                .thenThrow(new RuntimeException("User not found with id: "));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userController.updateUser(validToken, updateDTO);
        });

        assertEquals("User not found with id: ", exception.getMessage());
        verify(userService, times(1)).update(validToken, updateDTO);
    }

    @Test
    void updateUser_WithExistingEmail_ThrowsCustomException() {
        // Arrange
        when(userService.update(anyString(), any(UpdateDTO.class)))
                .thenThrow(new CustomException("Email is already in use"));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            userController.updateUser(validToken, updateDTO);
        });

        assertEquals("Email is already in use", exception.getMessage());
        verify(userService, times(1)).update(validToken, updateDTO);
    }
}

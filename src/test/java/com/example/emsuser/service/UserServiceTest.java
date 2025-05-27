package com.example.emsuser.service;

import com.example.emsuser.dto.UpdateDTO;
import com.example.emsuser.dto.UserLoginDTO;
import com.example.emsuser.dto.UserRegisterDTO;
import com.example.emsuser.dto.UserResponseDTO;
import com.example.emsuser.exception.CustomException;
import com.example.emsuser.model.UserModel;
import com.example.emsuser.model.UserRoleModel;
import com.example.emsuser.repository.UserRepository;
import com.example.emsuser.repository.UserRoleRepository;
import com.example.emsuser.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpServletResponse httpServletResponse;

    @InjectMocks
    private UserService userService;

    private UserRegisterDTO registerDTO;
    private UserLoginDTO loginDTO;
    private UpdateDTO updateDTO;
    private UserModel userModel;
    private UserRoleModel roleModel;
    private final UUID userId = UUID.randomUUID();
    private final String validToken = "valid.jwt.token";
    private final String encodedPassword = "encodedPassword123";

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

        // Setup user model
        userModel = new UserModel();
        userModel.setId(userId);
        userModel.setName("Test User");
        userModel.setEmail("test@example.com");
        userModel.setPassword(encodedPassword);

        // Setup role model
        roleModel = new UserRoleModel();
        roleModel.setRole("USER");
        roleModel.setUser(userModel);
        userModel.setRole(roleModel);

        // Mock repository responses
        when(userRepository.save(any(UserModel.class))).thenReturn(userModel);
        when(userRepository.findByEmail("test@example.com")).thenReturn(userModel);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userModel));
        
        // Mock password encoder
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
        when(passwordEncoder.matches(eq("password123"), eq(encodedPassword))).thenReturn(true);
        
        // Mock JWT token provider
        when(jwtTokenProvider.generateToken(any(UUID.class), anyString())).thenReturn(validToken);
        when(jwtTokenProvider.getUserIdFromJWT(validToken)).thenReturn(userId);
    }

    @Test
    void registerUser_WithNewEmail_ReturnsUserResponse() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        
        // Act
        ResponseEntity<UserResponseDTO> response = userService.registerUser(registerDTO);
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        UserResponseDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(registerDTO.getName(), responseBody.getName());
        assertEquals(registerDTO.getEmail(), responseBody.getEmail());
        assertEquals(registerDTO.getRole(), responseBody.getRole());
        
        verify(userRepository, times(1)).existsByEmail(registerDTO.getEmail());
        verify(passwordEncoder, times(1)).encode(registerDTO.getPassword());
        verify(userRepository, times(1)).save(any(UserModel.class));
        verify(userRoleRepository, times(1)).save(any(UserRoleModel.class));
    }

    @Test
    void registerUser_WithExistingEmail_ThrowsCustomException() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        
        // Act & Assert
        assertThrows(CustomException.class, () -> userService.registerUser(registerDTO));
        verify(userRepository, times(1)).existsByEmail(registerDTO.getEmail());
        verify(userRepository, never()).save(any(UserModel.class));
    }

//    @Test
//    void login_WithValidCredentials_ReturnsUserResponse() {
//        // Act
//        ResponseEntity<UserResponseDTO> response = userService.login(loginDTO, httpServletResponse);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//
//        UserResponseDTO responseBody = response.getBody();
//        assertNotNull(responseBody);
//        assertEquals(userModel.getName(), responseBody.getName());
//        assertEquals(userModel.getEmail(), responseBody.getEmail());
//        assertEquals(userModel.getRole().getRole(), responseBody.getRole());
//
//        verify(userRepository, times(1)).findByEmail(loginDTO.getEmail());
//        verify(passwordEncoder, times(1)).matches(loginDTO.getPassword(), userModel.getPassword());
//        verify(jwtTokenProvider, times(1)).generateToken(userModel.getId(), userModel.getRole().getRole());
//        verify(httpServletResponse, times(1)).addHeader(eq("Set-Cookie"), anyString());
//        verify(userRepository, times(1)).save(userModel);
//    }

    @Test
    void login_WithInvalidPassword_ThrowsRuntimeException() {
        // Arrange
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.login(loginDTO, httpServletResponse));
        verify(userRepository, times(1)).findByEmail(loginDTO.getEmail());
        verify(passwordEncoder, times(1)).matches(loginDTO.getPassword(), userModel.getPassword());
        verify(jwtTokenProvider, never()).generateToken(any(UUID.class), anyString());
    }

    @Test
    void update_WithValidData_ReturnsUpdatedUser() {
        // Act
        ResponseEntity<UserResponseDTO> response = userService.update(validToken, updateDTO);
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        UserResponseDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        
        verify(jwtTokenProvider, times(1)).getUserIdFromJWT(validToken);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(userModel);
    }

    @Test
    void update_WithExistingEmail_ThrowsCustomException() {
        // Arrange
        when(userRepository.existsByEmail(updateDTO.getEmail())).thenReturn(true);
        
        // Act & Assert
        assertThrows(CustomException.class, () -> userService.update(validToken, updateDTO));
        verify(jwtTokenProvider, times(1)).getUserIdFromJWT(validToken);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).existsByEmail(updateDTO.getEmail());
        verify(userRepository, never()).save(userModel);
    }

    @Test
    void getUserById_WithValidId_ReturnsUser() {
        // Act
        UserModel result = userService.getUserById(userId);
        
        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(userModel.getName(), result.getName());
        assertEquals(userModel.getEmail(), result.getEmail());
        
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_WithInvalidId_ThrowsRuntimeException() {
        // Arrange
        UUID invalidId = UUID.randomUUID();
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.getUserById(invalidId));
        verify(userRepository, times(1)).findById(invalidId);
    }
}
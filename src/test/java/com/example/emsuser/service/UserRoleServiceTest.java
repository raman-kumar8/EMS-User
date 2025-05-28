package com.example.emsuser.service;

import com.example.emsuser.model.UserModel;
import com.example.emsuser.model.UserRoleModel;
import com.example.emsuser.repository.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserRoleServiceTest {

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private UserRoleService userRoleService;

    private UserRoleModel roleModel;
    private UserModel userModel;
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup user model
        userModel = new UserModel();
        userModel.setId(userId);
        userModel.setName("Test User");
        userModel.setEmail("test@example.com");
        userModel.setPassword("encodedPassword123");

        // Setup role model
        roleModel = new UserRoleModel();
        roleModel.setRole("USER");
        roleModel.setUser(userModel);
        userModel.setRole(roleModel);

        // Mock repository responses
        when(userRoleRepository.save(any(UserRoleModel.class))).thenReturn(roleModel);
        when(userRoleRepository.findByUserId(userId)).thenReturn(Optional.of(roleModel));
    }

    @Test
    void createRole_SavesAndReturnsRole() {
        // Act
        UserRoleModel result = userRoleService.createRole(roleModel);

        // Assert
        assertNotNull(result);
        assertEquals(roleModel.getRole(), result.getRole());
        assertEquals(roleModel.getUser(), result.getUser());
        
        verify(userRoleRepository, times(1)).save(roleModel);
    }

    @Test
    void getRoleByUserId_WithExistingId_ReturnsRole() {
        // Act
        Optional<UserRoleModel> result = userRoleService.getRoleByUserId(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(roleModel.getRole(), result.get().getRole());
        assertEquals(roleModel.getUser(), result.get().getUser());
        
        verify(userRoleRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getRoleByUserId_WithNonExistingId_ReturnsEmptyOptional() {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        when(userRoleRepository.findByUserId(nonExistingId)).thenReturn(Optional.empty());
        
        // Act
        Optional<UserRoleModel> result = userRoleService.getRoleByUserId(nonExistingId);

        // Assert
        assertFalse(result.isPresent());
        verify(userRoleRepository, times(1)).findByUserId(nonExistingId);
    }
}
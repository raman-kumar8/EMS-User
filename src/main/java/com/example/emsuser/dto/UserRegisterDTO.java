package com.example.emsuser.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user registration.
 * Contains all necessary fields and validation for creating a new user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO {
    /**
     * User's full name
     */
    @NotNull(message = "Name is required")
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    /**
     * User's email address (used as username for login)
     */
    @NotNull(message = "Email is required")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Must be a valid email address format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    /**
     * User's password (must meet security requirements)
     */
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
        message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character"
    )
    private String password;

    /**
     * User's role (e.g., ADMIN, USER)
     */
    @NotBlank(message = "Role cannot be blank")
    private String role;
}

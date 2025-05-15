package com.example.emsuser.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user login.
 * Contains email and password fields for authentication.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {
    /**
     * User's email address (used as username)
     */
    @NotNull(message = "Email is required")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Must be a valid email address format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    /**
     * User's password
     */
    @NotNull(message = "Password is required")
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;
}

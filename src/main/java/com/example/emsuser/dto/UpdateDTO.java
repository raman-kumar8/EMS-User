package com.example.emsuser.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for updating user information.
 * Fields are optional to allow partial updates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDTO {
    /**
     * User's full name (optional)
     */
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    /**
     * User's email address (optional)
     */
    @Email(message = "Must be a valid email address format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;
}

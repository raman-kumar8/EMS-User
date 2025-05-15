package com.example.emsuser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for returning detailed user information.
 * Used for API responses when user details are requested.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsResponseDTO {
    /**
     * User's full name
     */
    private String name;
    
    /**
     * User's email address
     */
    private String email;
    
    /**
     * User's role in the system
     */
    private String role;
}
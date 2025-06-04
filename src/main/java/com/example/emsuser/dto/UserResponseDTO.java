package com.example.emsuser.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserResponseDTO {
    private UUID id;

    private String name;
    private String email;
    private String role;
    public UserResponseDTO()
    {

    }
    public UserResponseDTO(String name, String email, String role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public UserResponseDTO(UUID id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}

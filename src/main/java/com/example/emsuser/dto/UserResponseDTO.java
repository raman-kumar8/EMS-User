package com.example.emsuser.dto;

import lombok.Data;

@Data
public class UserResponseDTO {

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
}

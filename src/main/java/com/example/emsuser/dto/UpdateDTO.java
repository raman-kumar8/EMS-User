package com.example.emsuser.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateDTO {
    @NotNull(message = "Name cannot be blank")
    @Min(value = 3, message = "Name must be at least 3 characters long")
    @Max(value = 100, message = "Name must be at most 100 characters long")
    private String name;
    @Email
    @NotNull(message = "Email cannot be blank")
    private String email;
    @NotNull(message = "Role cannot be blank")
    private String role;
}

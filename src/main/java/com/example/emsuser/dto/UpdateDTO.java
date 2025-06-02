package com.example.emsuser.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateDTO {
    @Min(value = 3, message = "Name must be at least 3 characters long")
    @Max(value = 100, message = "Name must be at most 100 characters long")
    private String name;
    @Email
    private String email;

}

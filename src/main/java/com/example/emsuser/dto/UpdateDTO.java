package com.example.emsuser.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateDTO {


    private String name;
    @Email
    private String email;
}

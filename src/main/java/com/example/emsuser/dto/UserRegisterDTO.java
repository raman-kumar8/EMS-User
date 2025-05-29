package com.example.emsuser.dto;

import com.example.emsuser.validation.PasswordMatches;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;

@PasswordMatches
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO {
    @NotNull(message = "Name is required")
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @NotNull(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Confirm password cannot be blank")
    private String confirmPassword;



    @NotBlank(message = "Role cannot be blank")
    private String role;
}

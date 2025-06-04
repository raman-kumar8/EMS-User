package com.example.emsuser.controller;

import com.example.emsuser.dto.*;
import com.example.emsuser.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.Duration;
import java.util.List;


@RestController
@RequestMapping("users")
@RequiredArgsConstructor

public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<  UserResponseDTO >registerUser(@Valid @RequestBody  UserRegisterDTO userRegisterDTO) {

        return userService.registerUser(userRegisterDTO);
    }

    @PostMapping("/login")

    @ResponseStatus(HttpStatus.OK)

    public ResponseEntity<UserResponseDTO> loginUser(

            @RequestBody @Valid UserLoginDTO loginDTO,

            HttpServletResponse response

    ) {

        return userService.login(loginDTO, response);

    }
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {


        ResponseCookie jwtCookie = ResponseCookie.from("jwt_token", null)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", jwtCookie.toString());


        return ResponseEntity.ok("Logged out successfully");
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponseDTO> updateUser(@CookieValue("jwt_token") String token, @RequestBody UpdateDTO updateDTO) {

        return userService.update(token,updateDTO);

    }

    @PostMapping("/forget")
    public String forgotPassword(@RequestBody PasswordResetRequestDTO passwordResetRequestDTO) {
        return userService.forgotPassword(passwordResetRequestDTO);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword( @RequestBody PasswordResetDTO passwordResetDTO)
    {
        return userService.resetPassword( passwordResetDTO);

    }

    @GetMapping("/getAll")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return userService.getAllUsers();
    }





}

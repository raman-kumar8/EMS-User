package com.example.emsuser.service;

import com.example.emsuser.dto.*;
import com.example.emsuser.exception.CustomException;
import com.example.emsuser.model.UserModel;
import com.example.emsuser.model.UserRoleModel;
import com.example.emsuser.repository.UserRepository;
import com.example.emsuser.repository.UserRoleRepository;
import com.example.emsuser.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Duration;
import java.util.*;

@Service
public class
UserService {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    EmailService emailService ;


    public ResponseEntity<UserResponseDTO> registerUser(UserRegisterDTO userRegisterDTO) {

        if (userRepository.existsByEmail(userRegisterDTO.getEmail())) {
            throw new CustomException("Email is already in use");
        }

        String encodedPassword = passwordEncoder.encode(userRegisterDTO.getPassword());

        UserModel user = new UserModel();
        user.setName(userRegisterDTO.getName());
        user.setEmail(userRegisterDTO.getEmail());
        user.setPassword(encodedPassword);

        // Save user first so it gets an ID (needed for FK in role)
        UserModel savedUser = userRepository.save(user);

        // Create and assign role
        UserRoleModel userRole = new UserRoleModel();
        userRole.setUser(savedUser);
        userRole.setRole(userRegisterDTO.getRole());
        userRoleRepository.save(userRole);


     UserResponseDTO responseDTO = new UserResponseDTO(savedUser.getName(), savedUser.getEmail(), userRole.getRole());
        return ResponseEntity.ok().body(responseDTO);
    }


    public ResponseEntity<UserResponseDTO> login(@RequestBody UserLoginDTO loginDTO, HttpServletResponse response) {

        Optional<UserModel> userOpt = Optional.ofNullable(userRepository.findByEmail(loginDTO.getEmail()));

        UserModel user = userOpt.orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {

            throw new RuntimeException("Invalid password");

        }


        String token = jwtTokenProvider.generateToken(user.getId(), String.valueOf(user.getRole()));



        ResponseCookie jwtCookie = ResponseCookie.from("jwt_token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Lax") // <--- THIS IS WHAT YOU'RE MISSING
                .build();

        response.addHeader("Set-Cookie", jwtCookie.toString());


        user.setLastLogin(new Date());

        userRepository.save(user);

        UserResponseDTO responseDTO = new UserResponseDTO(user.getName(), user.getEmail(), user.getRole().getRole());

        return ResponseEntity.ok().body(responseDTO);

    }




    public ResponseEntity<UserResponseDTO> update(String token, UpdateDTO updateDTO) {

       UUID userId = jwtTokenProvider.getUserIdFromJWT(token);
       if(userId==null)
       {
           throw new CustomException("Invalid token");
       }

            UserModel user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: "));




            // Update only non-null fields from DTO
            if (updateDTO.getName() != null && !updateDTO.getName().equals(user.getName())) {
                user.setName(updateDTO.getName());
            }


        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail()) && !updateDTO.getEmail().equals("")) {
            if (userRepository.existsByEmail(updateDTO.getEmail())) {
                throw new CustomException("Email is already in use");
            }
            user.setEmail(updateDTO.getEmail());
        }



            // Save the updated user
            UserModel updatedUser = userRepository.save(user);

            // Convert to DTO and return
            UserResponseDTO responseDTO = new UserResponseDTO();
            responseDTO.setName(updatedUser.getName());
            responseDTO.setEmail(updatedUser.getEmail());
            responseDTO.setRole(updatedUser.getRole().getRole());

            return ResponseEntity.ok(responseDTO);


    }
public UserModel getUserById(UUID userId){
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: "));
}


    public void setLeaveCount(UUID userId, int leaveCount)
    {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: "));
        user.setLeaveCount(leaveCount);
        userRepository.save(user);
    }

    public String forgotPassword(PasswordResetRequestDTO passwordResetRequestDTO) {
        UserModel userModel = userRepository.findByEmail(passwordResetRequestDTO.getEmail());
        if(userModel == null)
            throw new CustomException("No User Found with this Email");

        StringBuilder str = new StringBuilder();
        str.append("https://ems.radhexdeveloper.life/reset-password?token="+userModel.getId());

        emailService.sendHtmlEmail(userModel.getEmail(), str.toString());
        return "Mail Send";
    }

    public ResponseEntity<String> resetPassword(PasswordResetDTO passwordResetDTO) {
        UserModel userModel = userRepository.findById(UUID.fromString(passwordResetDTO.getToken())).orElseThrow(() -> new CustomException("User not found with id: "));
        userModel.setPassword(passwordEncoder.encode(passwordResetDTO.getNewPassword()));
        userRepository.save(userModel);
        return ResponseEntity.ok().body("Password Reset Successfully ");
    }
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {

        List<UserModel> users = userRepository.findAll();

        List<UserResponseDTO> userDTOs = users.stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole() != null ? user.getRole().getRole() : "No Role"
                ))
                .toList();

        return ResponseEntity.ok(userDTOs);
    }
}


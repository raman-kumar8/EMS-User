package com.example.emsuser.controller;

import com.example.emsuser.dto.UpdateLeaveCountDTO;
import com.example.emsuser.dto.UserDetailsResponseDTo;
import com.example.emsuser.dto.UserResponseLeaveDTO;
import com.example.emsuser.exception.CustomException;
import com.example.emsuser.model.UserModel;
import com.example.emsuser.repository.UserRepository;
import com.example.emsuser.security.JwtTokenProvider;
import com.example.emsuser.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/general")
public class GeneralController {
    @Autowired
    UserService userService;
   @Autowired
   private JwtTokenProvider jwtTokenProvider;
    @GetMapping("/validate")
    public String validate(@CookieValue("jwt_token") String token) {


        UUID userId = jwtTokenProvider.getUserIdFromJWT(token);
        if(userId==null){
            throw new CustomException("Invalid token");
        }

        return userId.toString();

    }
    @GetMapping("/user")
    public ResponseEntity<UserDetailsResponseDTo> getUserDetails(@CookieValue("jwt_token") String token){
        UUID userId = jwtTokenProvider.getUserIdFromJWT(token);
        UserModel user = userService.getUserById(userId);
        UserDetailsResponseDTo response = new UserDetailsResponseDTo();
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().getRole());
        response.setLeaveCount(user.getLeaveCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user1")
    public ResponseEntity<UserResponseLeaveDTO> getUserById(@RequestParam UUID id)
    {
        UserModel user = userService.getUserById(id);
        UserResponseLeaveDTO response = new UserResponseLeaveDTO();
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().getRole());
        response.setLeaveCount(user.getLeaveCount());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/setLeaveCount")
    public void setLeaveCount(@RequestBody UpdateLeaveCountDTO updateLeaveCountDTO)
    {
        userService.setLeaveCount(updateLeaveCountDTO.getUserId(),updateLeaveCountDTO.getLeaveCount());
    }

}

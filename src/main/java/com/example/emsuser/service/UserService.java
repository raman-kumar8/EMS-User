package com.example.emsuser.service;

import com.example.emsuser.dto.UserRegisterDTO;
import com.example.emsuser.dto.UserResponseDTO;
import com.example.emsuser.model.UserModel;
import com.example.emsuser.model.UserRoleModel;
import com.example.emsuser.repository.UserRepository;
import com.example.emsuser.repository.UserRoleRepository;
import com.example.emsuser.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired

    private PasswordEncoder passwordEncoder;


    public UserResponseDTO registerUser(UserRegisterDTO userRegisterDTO) {
        if (userRepository.existsByEmail(userRegisterDTO.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        String encodedPassword = passwordEncoder.encode(userRegisterDTO.getPassword());

        UserModel user = new UserModel();
        user.setName(userRegisterDTO.getName());
        user.setEmail(userRegisterDTO.getEmail());
        user.setPassword(encodedPassword);

        // Save user without token first (we need user ID for token)
        UserModel savedUser = userRepository.save(user);

        // Create role
        UserRoleModel userRole = new UserRoleModel();
        userRole.setUser(savedUser);
        userRole.setRole(userRegisterDTO.getRole());
        userRoleRepository.save(userRole);

        // Now generate JWT token using user ID and role
        String jwtToken = jwtTokenProvider.generateToken(savedUser.getId(), userRegisterDTO.getRole());
        savedUser.setToken(jwtToken);
        savedUser.setTokenExpiry(new Date(System.currentTimeMillis() + 86400000)); // 1 day
        savedUser.setLastLogin(new Date());

        // Save user again with token info
        userRepository.save(savedUser);

        return new UserResponseDTO(savedUser.getName(), savedUser.getEmail(), userRole.getRole());
    }

}

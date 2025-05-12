package com.example.emsuser.service;

import com.example.emsuser.model.UserRoleModel;
import com.example.emsuser.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    @Autowired
    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    // Save a new role
    public UserRoleModel createRole(UserRoleModel role) {
        return  userRoleRepository.save(role);
    }



    // Get role by user ID
    public Optional<UserRoleModel> getRoleByUserId(UUID userId) {
        return userRoleRepository.findByUserId(userId);
    }

}

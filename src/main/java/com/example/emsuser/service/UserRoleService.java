package com.example.emsuser.service;

import com.example.emsuser.model.UserRoleModel;
import com.example.emsuser.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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




    // Get role by ID
    public Optional<UserRoleModel> getRoleById(Long id) {
        return userRoleRepository.findById(id);
    }
}

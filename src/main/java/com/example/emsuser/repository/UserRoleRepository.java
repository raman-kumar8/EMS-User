package com.example.emsuser.repository;

import com.example.emsuser.model.UserModel;
import com.example.emsuser.model.UserRoleModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRoleModel, Long> {
    Optional<UserRoleModel> findByUserId(UUID userId);

}

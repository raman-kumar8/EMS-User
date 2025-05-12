package com.example.emsuser.model;

import com.example.emsuser.model.UserModel;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserRoleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Make this ID auto-generated (or UUID if needed)

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserModel user;


    @Column(nullable = false)
    private String role;  // Role field for storing the user's role
}

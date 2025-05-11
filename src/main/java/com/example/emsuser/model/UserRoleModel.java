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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)  // Foreign key to User_Model
    private UserModel user;  // Link the User_Role_Model to the User_Model

    @Column(nullable = false)
    private String role;  // Role field for storing the user's role
}

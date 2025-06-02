package org.example.leavesystem.DTO;

import lombok.Data;

@Data
public class UserDetailsResponseDTo {
    private String name;
    private String email;
    private String role;
    private int leaveCount;
}

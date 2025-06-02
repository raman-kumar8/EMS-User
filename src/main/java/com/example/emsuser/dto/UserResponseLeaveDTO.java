package com.example.emsuser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseLeaveDTO {
    private String name;
    private String email;
    private String role;
    private int leaveCount;

}

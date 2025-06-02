package com.example.emsuser.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateLeaveCountDTO {
    private UUID userId;
    private int leaveCount;
}

package org.example.leavesystem.DTO;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserDetailAndResponeListDTO {
    private UUID userId;
    private UserDetailsResponseDTo userDetails;
    private List<ResponseListDTO> responseListDTOList;
}

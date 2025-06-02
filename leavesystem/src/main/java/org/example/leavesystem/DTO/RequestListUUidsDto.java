package org.example.leavesystem.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RequestListUUidsDto {
   @NotNull(message = "uuids cannot be null")
List<UUID> uuids;
}

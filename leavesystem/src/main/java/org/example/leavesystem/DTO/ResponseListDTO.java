package org.example.leavesystem.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object for returning a list of leave requests.
 * Contains essential information about each leave request.
 */
@Data
public class ResponseListDTO {

    private UUID leaveId;
    /**
     * The reason provided by the employee for requesting leave.
     */
    private String reason;
    
    /**
     * Current status of the leave request (PENDING, APPROVED, REJECTED).
     */
    private String status;
    
    /**
     * The date when the leave period starts.
     */
    private LocalDate startDate;
    
    /**
     * The date when the leave period ends.
     */
    private LocalDate endDate;
}
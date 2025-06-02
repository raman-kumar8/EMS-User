package org.example.leavesystem.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object for API responses.
 * Used to return information about leave requests and operation status.
 */
@Data
public class ResponseDTO {
    private UUID leaveId;
    /**
     * Message describing the result of the operation.
     */
    private String message;

    /**
     * Status of the leave request (PENDING, APPROVED, REJECTED).
     */
    private String status;

    /**
     * The start date of the leave period.
     */
    private LocalDate startDate;

    /**
     * The end date of the leave period.
     */
    private LocalDate endDate;

    /**
     * The date when the leave request was submitted.
     */
    private LocalDate appliedDate;
    
}

package org.example.leavesystem.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object for leave application requests.
 * Contains all necessary information for applying for a leave.
 */
@Data
public class LeaveInsertDTO {
    /**
     * The start date of the leave period.
     * Must not be null and should be a present or future date.
     */
    @NotNull(message = "Start Date is mandatory")
    private LocalDate startDate;

    /**
     * The end date of the leave period.
     * Must not be null and should be after or equal to the start date.
     */
    @NotNull(message = "End Date is mandatory")
    private LocalDate endDate;

    /**
     * The reason for requesting leave.
     * Must not be blank and should be between 10 and 255 characters.
     */
    @NotNull(message = "Reason is mandatory")
    @Size(min = 10, max = 255, message = "Reason should be between 10 and 255 characters")
    private String reason;

    /**
     * Validates that the start date is before or equal to the end date.
     * 
     * @return true if the dates are valid, false otherwise
     */
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) {
            return false;
        }
        return !startDate.isAfter(endDate);
    }

    /**
     * Validates that the start date is not in the past.
     * 
     * @return true if the start date is valid, false otherwise
     */
    public boolean isValidStartDate() {
        if (startDate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return !startDate.isBefore(today);
    }
}

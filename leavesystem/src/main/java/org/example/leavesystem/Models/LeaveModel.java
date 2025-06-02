package org.example.leavesystem.Models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity class representing a leave request in the system.
 * This model stores all information related to an employee's leave application.
 */
@Entity
@Data
public class LeaveModel {
    /**
     * Unique identifier for the leave request.
     * Generated automatically as a UUID.
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID leaveId;

    /**
     * ID of the user who applied for the leave.
     */
    private UUID userId;

    /**
     * The date when the leave period starts.
     */
    private LocalDate startDate;

    /**
     * The date when the leave period ends.
     */
    private LocalDate endDate;

    /**
     * The reason provided by the employee for requesting leave.
     */
    private String reason;

    /**
     * Current status of the leave request (PENDING, APPROVED, REJECTED).
     */
    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    /**
     * The date when the leave request was submitted.
     */
    private LocalDate appliedDate;

    /**
     * ID of the admin who reviewed the leave request.
     * This will be null until the request is reviewed.
     */
    private UUID adminId;

    /**
     * The date when the leave request was reviewed by an admin.
     * This will be null until the request is reviewed.
     */
    private LocalDate reviewedDate;
}

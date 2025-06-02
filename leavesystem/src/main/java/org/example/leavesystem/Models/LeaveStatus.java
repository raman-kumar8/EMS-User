package org.example.leavesystem.Models;

/**
 * Enum representing the possible statuses of a leave request.
 */
public enum LeaveStatus {
    /**
     * Leave request is awaiting review by an admin.
     */
    PENDING,

    /**
     * Leave request has been approved by an admin.
     */
    APPROVED,

    /**
     * Leave request has been rejected by an admin.
     */
    REJECTED;
}

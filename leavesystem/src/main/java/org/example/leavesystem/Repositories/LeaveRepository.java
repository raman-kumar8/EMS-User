package org.example.leavesystem.Repositories;

import org.example.leavesystem.Models.LeaveModel;
import org.example.leavesystem.Models.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for LeaveModel entities.
 * Provides methods for querying leave requests.
 */
public interface LeaveRepository extends JpaRepository<LeaveModel, UUID> {
    /**
     * Finds all leave requests for a specific user.
     *
     * @param userId The ID of the user
     * @return List of leave requests for the user
     */
    @Query("SELECT t FROM LeaveModel t WHERE t.userId = :userId")
    List<LeaveModel> findByUserId(@Param("userId") UUID userId);
    List<LeaveModel> findByStatus(LeaveStatus status);

}

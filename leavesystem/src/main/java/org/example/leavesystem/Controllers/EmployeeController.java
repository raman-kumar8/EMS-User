package org.example.leavesystem.Controllers;

import jakarta.validation.Valid;
import org.example.leavesystem.Controllers.OpenFeign.Validate;
import org.example.leavesystem.DTO.*;
import org.example.leavesystem.Services.EmployeeLeaveService;
import org.example.leavesystem.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * newsletterController for employee leave management operations.
 * Provides endpoints for applying for leave and retrieving leave history.
 */
@RestController
@RequestMapping("/api/v1/")
public class EmployeeController {
    private final EmployeeLeaveService employeeLeaveService;
    private final Validate validate;

    /**
     * Constructor for dependency injection.
     * 
     * @param employeeLeaveService Service for leave management operations
     * @param validate Service for token validation
     */
    @Autowired
    public EmployeeController(EmployeeLeaveService employeeLeaveService, Validate validate) {
        this.employeeLeaveService = employeeLeaveService;
        this.validate = validate;
    }

    /**
     * Endpoint for applying for leave.
     * 
     * @param token JWT token from cookie for authentication
     * @param leaveInsertDTO Leave request data
     * @return Response with leave application status
     */
    @PostMapping("/leave/apply")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseDTO> applyLeave(
            @CookieValue("jwt_token") String token, 
            @Valid @RequestBody LeaveInsertDTO leaveInsertDTO) {

        UUID userId = getUserIdFromToken(token);

        // Additional validation for business rules
        if (!leaveInsertDTO.isValidDateRange()) {
            throw new CustomException("End date must be after or equal to start date");
        }

        if (!leaveInsertDTO.isValidStartDate()) {
            throw new CustomException("Not a valid Start Date");
        }

        UserDetailsResponseDTo userDetailsResponseDTo = validate.getUserByID(userId).getBody();
        int days = Math.toIntExact(ChronoUnit.DAYS.between(leaveInsertDTO.getStartDate(), leaveInsertDTO.getEndDate()));
        if( userDetailsResponseDTo.getLeaveCount() < days)
        {
            throw new CustomException("Limit of Leave Exceded ");
        }



        return employeeLeaveService.add(userId, leaveInsertDTO);
    }

    /**
     * Endpoint for retrieving all leaves for the authenticated user.
     * 
     * @param token JWT token from cookie for authentication
     * @return List of leave requests
     */
    @GetMapping("/leave/getAll")
    public ResponseEntity<List<ResponseListDTO>> getAllLeaves(@CookieValue("jwt_token") String token) {
        UUID userId = getUserIdFromToken(token);
        return employeeLeaveService.getUserAll(userId);
    }

    //Admin Routes Below

    @GetMapping("/admin/getAll")
    @ResponseStatus(HttpStatus.ACCEPTED)

    public ResponseEntity<List<UserDetailAndResponeListDTO>> getAll(@CookieValue("jwt_token") String token)
    {
        boolean isPending = true;
      UUID userId = getUserIdFromToken(token);
      UserDetailsResponseDTo userDetailsResponseDTo = validate.getUserDetails(token).getBody();
//      String role = userDetailsResponseDTo.getRole();
//      if(!role.equals("ADMIN"))
      //  throw new IllegalArgumentException("Invalid role Not Admin");
        return employeeLeaveService.getAll(isPending);

    }

    @PutMapping("/admin/leave/reject")
    public ResponseEntity<ResponseDTO> reject(@RequestParam("id") UUID leaveId)
    {
       return  employeeLeaveService.reject(leaveId);
    }

    @PutMapping("/admin/leave/approve")
    public ResponseEntity<ResponseDTO> approve(@RequestParam("id") UUID leaveId)
    {
        return  employeeLeaveService.approve(leaveId);
    }



    /**
     * Helper method to extract user ID from JWT token.
     * 
     * @param token JWT token
     * @return User ID as UUID
     * @throws IllegalArgumentException if token is invalid
     */
    private UUID getUserIdFromToken(String token) {
        String cookieHeader = "jwt_token=" + token;
        String userIdString = validate.validate(cookieHeader);

        try {
            return UUID.fromString(userIdString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user ID format in token");
        }
    }

    /**
     * Exception handler for validation errors.
     * 
     * @param e Exception
     * @return Error response
     */

}

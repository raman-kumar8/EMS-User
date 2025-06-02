package org.example.leavesystem.Services;

import jakarta.validation.Valid;
import org.example.leavesystem.Controllers.OpenFeign.Validate;
import org.example.leavesystem.DTO.*;
import org.example.leavesystem.Models.LeaveModel;
import org.example.leavesystem.Models.LeaveStatus;
import org.example.leavesystem.Repositories.LeaveRepository;
import org.example.leavesystem.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for employee leave management operations.
 * Handles business logic for applying for leave and retrieving leave history.
 */
@Service
public class EmployeeLeaveService {
    private final LeaveRepository leaveRepository;
    private Validate validateClient ;

    /**
     * Constructor for dependency injection.
     * 
     * @param leaveRepository Repository for leave data operations
     */
    @Autowired
    public EmployeeLeaveService(Validate validate,LeaveRepository leaveRepository) {
        this.leaveRepository = leaveRepository;
        this.validateClient = validate;
    }

    /**
     * Adds a new leave request for a user.
     * 
     * @param userId The ID of the user applying for leave
     * @param leaveInsertDTO Leave request data
     * @return Response with leave application status
     */
    public ResponseEntity<ResponseDTO> add(UUID userId, LeaveInsertDTO leaveInsertDTO) {
       LocalDate today   = LocalDate.now();

       if(leaveInsertDTO.getEndDate().isBefore(today) || leaveInsertDTO.getStartDate().isBefore(today))
       throw new CustomException("Date in Past Are Not Allowed");

       if(leaveInsertDTO.getStartDate().isAfter(leaveInsertDTO.getEndDate()))
       throw new CustomException("Start Date Can't Be Greater Than End Date");
        LeaveModel leaveModel = new LeaveModel();

        // Set data
        leaveModel.setUserId(userId);
        leaveModel.setReason(leaveInsertDTO.getReason());
        leaveModel.setEndDate(leaveInsertDTO.getEndDate());
        leaveModel.setStatus(LeaveStatus.PENDING);
        leaveModel.setAppliedDate(LocalDate.now());
        leaveModel.setStartDate(leaveInsertDTO.getStartDate());

       LeaveModel leaveModel1 =  leaveRepository.save(leaveModel);

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setLeaveId(leaveModel1.getLeaveId());
        responseDTO.setStartDate(leaveInsertDTO.getStartDate());
        responseDTO.setEndDate(leaveInsertDTO.getEndDate());
        responseDTO.setAppliedDate(LocalDate.now());
        responseDTO.setMessage("Leave applied successfully");
        responseDTO.setStatus(LeaveStatus.PENDING.toString());

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Retrieves all leave requests for a user.
     * 
     * @param userId The ID of the user
     * @return List of leave requests for the user
     */
    public ResponseEntity<List<ResponseListDTO>> getUserAll(UUID userId) {
        List<LeaveModel> leaveModels = leaveRepository.findByUserId(userId);


        List<ResponseListDTO> responseList = leaveModels.stream().map(leave -> {
            ResponseListDTO dto = new ResponseListDTO();
            dto.setLeaveId(leave.getLeaveId());
            dto.setReason(leave.getReason());
            dto.setStatus(leave.getStatus().toString());
            dto.setStartDate(leave.getStartDate());
            dto.setEndDate(leave.getEndDate());
            return dto;
        }).toList();

        return ResponseEntity.ok( responseList);
    }


    //Admin Routes Below

    public ResponseEntity<List<UserDetailAndResponeListDTO>> getAll(boolean isPending) {
        List<LeaveModel> leaveModels;
        if(!isPending)
        {
            leaveModels = leaveRepository.findAll();
        }
        else
        {
             leaveModels = leaveRepository.findByStatus(LeaveStatus.PENDING);

        }

        Map<UUID, List<LeaveModel>> groupedByUser = leaveModels.stream()
                .collect(Collectors.groupingBy(LeaveModel::getUserId));

        List<UserDetailAndResponeListDTO> result = new ArrayList<>();

        for (Map.Entry<UUID, List<LeaveModel>> entry : groupedByUser.entrySet()) {
            UUID userId = entry.getKey();
            List<LeaveModel> userLeaves = entry.getValue();


            UserDetailsResponseDTo userDetails;
            try {
                userDetails = validateClient.getUserByID(userId).getBody();
            } catch (Exception e) {
                continue; // skip this user if details cannot be fetched
            }

            List<ResponseListDTO> leaveDTOs = userLeaves.stream().map(leave -> {
                ResponseListDTO dto = new ResponseListDTO();
                dto.setLeaveId(leave.getLeaveId());
                dto.setReason(leave.getReason());
                dto.setStatus(leave.getStatus().toString());
                dto.setStartDate(leave.getStartDate());
                dto.setEndDate(leave.getEndDate());
                return dto;
            }).toList();

            UserDetailAndResponeListDTO response = new UserDetailAndResponeListDTO();
            response.setUserId(userId);
            response.setUserDetails(userDetails);
            response.setResponseListDTOList(leaveDTOs);

            result.add(response);
        }

        return ResponseEntity.ok(result);
    }

    public ResponseEntity<ResponseDTO> reject(UUID leaveId)
    {
        LeaveModel leaveModel = leaveRepository.findById(leaveId).orElseThrow( () -> new IllegalArgumentException("Invalid leave ID") );
        leaveModel.setStatus(LeaveStatus.REJECTED);
        leaveRepository.save(leaveModel);
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setLeaveId(leaveId);
        responseDTO.setMessage("Leave Rejected Successfully");
        responseDTO.setStatus(LeaveStatus.REJECTED.toString());
        responseDTO.setStartDate(leaveModel.getStartDate());
        responseDTO.setEndDate(leaveModel.getEndDate());
        responseDTO.setAppliedDate(LocalDate.now());
        return ResponseEntity.accepted().body( responseDTO);
    }
    public ResponseEntity<ResponseDTO> approve(UUID leaveId)
    {
        LeaveModel leaveModel = leaveRepository.findById(leaveId).orElseThrow( () -> new IllegalArgumentException("Invalid leave ID") );
        leaveModel.setStatus(LeaveStatus.APPROVED);
        leaveRepository.save(leaveModel);
        UserDetailsResponseDTo userDetailsResponseDTo = validateClient.getUserByID(leaveModel.getUserId()).getBody();
        int days = Math.toIntExact(ChronoUnit.DAYS.between(leaveModel.getStartDate(), leaveModel.getEndDate()));
        UpdateLeaveCountDTO updateLeaveCountDTO = new UpdateLeaveCountDTO();
        updateLeaveCountDTO.setUserId(leaveModel.getUserId());

        if(days < userDetailsResponseDTo.getLeaveCount())
        {
            updateLeaveCountDTO.setLeaveCount(userDetailsResponseDTo.getLeaveCount() - days);
        }
        else
        {
            throw new CustomException("Limit Of Employee Leave Exceded");
        }
        validateClient.setLeaveCount(updateLeaveCountDTO);

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setLeaveId(leaveId);
        responseDTO.setMessage("Leave Approved Successfully");
        responseDTO.setStatus(LeaveStatus.APPROVED.toString());
        responseDTO.setStartDate(leaveModel.getStartDate());
        responseDTO.setEndDate(LocalDate.now());
        responseDTO.setAppliedDate(leaveModel.getAppliedDate());
        return ResponseEntity.accepted().body( responseDTO);
    }





}

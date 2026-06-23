package com.leavemanagement.controller;

import com.leavemanagement.dto.request.LeaveActionRequest;
import com.leavemanagement.dto.response.ApiResponse;
import com.leavemanagement.dto.response.EmployeeResponse;
import com.leavemanagement.dto.response.LeaveResponse;
import com.leavemanagement.entity.Employee;
import com.leavemanagement.repository.EmployeeRepository;
import com.leavemanagement.service.EmployeeService;
import com.leavemanagement.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;
    private final EmployeeRepository employeeRepository;

    @GetMapping("/leave/pending")
    public ResponseEntity<ApiResponse<List<LeaveResponse>>> getPendingApprovals(Authentication authentication) {
        List<LeaveResponse> response = leaveService.getPendingApprovals(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Pending approvals retrieved successfully", response));
    }

    @PutMapping("/leave/approve/{id}")
    public ResponseEntity<ApiResponse<LeaveResponse>> approveLeave(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody(required = false) LeaveActionRequest request) {
        String remarks = request != null ? request.getRemarks() : null;
        LeaveResponse response = leaveService.approveLeave(authentication.getName(), id, remarks);
        return ResponseEntity.ok(ApiResponse.success("Leave approved successfully", response));
    }

    @PutMapping("/leave/reject/{id}")
    public ResponseEntity<ApiResponse<LeaveResponse>> rejectLeave(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody(required = false) LeaveActionRequest request) {
        String remarks = request != null ? request.getRemarks() : null;
        LeaveResponse response = leaveService.rejectLeave(authentication.getName(), id, remarks);
        return ResponseEntity.ok(ApiResponse.success("Leave rejected successfully", response));
    }

    @GetMapping("/team")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getTeamMembers(Authentication authentication) {
        Employee manager = employeeRepository.findByEmail(authentication.getName())
                .orElseThrow();
        List<EmployeeResponse> team = employeeRepository.findByManagerId(manager.getId())
                .stream().map(EmployeeService::mapToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Team members retrieved successfully", team));
    }

    @GetMapping("/leave/team")
    public ResponseEntity<ApiResponse<List<LeaveResponse>>> getTeamLeaves(Authentication authentication) {
        List<LeaveResponse> response = leaveService.getTeamLeaves(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Team leaves retrieved successfully", response));
    }
}

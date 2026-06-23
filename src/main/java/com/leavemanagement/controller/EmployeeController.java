package com.leavemanagement.controller;

import com.leavemanagement.dto.request.LeaveRequest;
import com.leavemanagement.dto.response.ApiResponse;
import com.leavemanagement.dto.response.EmployeeResponse;
import com.leavemanagement.dto.response.LeaveBalanceResponse;
import com.leavemanagement.dto.response.LeaveResponse;
import com.leavemanagement.service.EmployeeService;
import com.leavemanagement.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final LeaveService leaveService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getProfile(Authentication authentication) {
        EmployeeResponse response = employeeService.getProfile(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", response));
    }

    @PostMapping("/leave/apply")
    public ResponseEntity<ApiResponse<LeaveResponse>> applyLeave(
            Authentication authentication,
            @Valid @RequestBody LeaveRequest request) {
        LeaveResponse response = leaveService.applyLeave(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Leave application submitted successfully", response));
    }

    @GetMapping("/leave/history")
    public ResponseEntity<ApiResponse<List<LeaveResponse>>> getLeaveHistory(Authentication authentication) {
        List<LeaveResponse> response = leaveService.getLeaveHistory(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Leave history retrieved successfully", response));
    }

    @GetMapping("/leave/balance")
    public ResponseEntity<ApiResponse<List<LeaveBalanceResponse>>> getLeaveBalance(Authentication authentication) {
        List<LeaveBalanceResponse> response = leaveService.getLeaveBalance(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Leave balance retrieved successfully", response));
    }

    @PutMapping("/leave/cancel/{id}")
    public ResponseEntity<ApiResponse<LeaveResponse>> cancelLeave(
            Authentication authentication,
            @PathVariable Long id) {
        LeaveResponse response = leaveService.cancelLeave(authentication.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("Leave cancelled successfully", response));
    }
}

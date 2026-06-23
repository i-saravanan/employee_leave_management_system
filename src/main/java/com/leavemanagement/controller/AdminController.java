package com.leavemanagement.controller;

import com.leavemanagement.dto.request.RegisterRequest;
import com.leavemanagement.dto.response.ApiResponse;
import com.leavemanagement.dto.response.EmployeeResponse;
import com.leavemanagement.dto.response.LeaveResponse;
import com.leavemanagement.enums.LeaveStatus;
import com.leavemanagement.service.AdminService;
import com.leavemanagement.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final LeaveService leaveService;

    @GetMapping("/employees")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getAllEmployees() {
        List<EmployeeResponse> response = adminService.getAllEmployees();
        return ResponseEntity.ok(ApiResponse.success("Employees retrieved successfully", response));
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(@PathVariable Long id) {
        EmployeeResponse response = adminService.getEmployeeById(id);
        return ResponseEntity.ok(ApiResponse.success("Employee retrieved successfully", response));
    }

    @PostMapping("/employees")
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(
            @Valid @RequestBody RegisterRequest request) {
        EmployeeResponse response = adminService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee created successfully", response));
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @PathVariable Long id,
            @RequestBody RegisterRequest request) {
        EmployeeResponse response = adminService.updateEmployee(id, request);
        return ResponseEntity.ok(ApiResponse.success("Employee updated successfully", response));
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateEmployee(@PathVariable Long id) {
        adminService.deactivateEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee deactivated successfully"));
    }

    @GetMapping("/leaves")
    public ResponseEntity<ApiResponse<List<LeaveResponse>>> getAllLeaves(
            @RequestParam(required = false) String status) {
        List<LeaveResponse> response;
        if (status != null && !status.isBlank()) {
            response = leaveService.getLeavesByStatus(LeaveStatus.valueOf(status.toUpperCase()));
        } else {
            response = leaveService.getAllLeaves();
        }
        return ResponseEntity.ok(ApiResponse.success("Leaves retrieved successfully", response));
    }
}

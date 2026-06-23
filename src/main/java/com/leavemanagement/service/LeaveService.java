package com.leavemanagement.service;

import com.leavemanagement.dto.request.LeaveRequest;
import com.leavemanagement.dto.response.LeaveBalanceResponse;
import com.leavemanagement.dto.response.LeaveResponse;
import com.leavemanagement.entity.Employee;
import com.leavemanagement.entity.LeaveApplication;
import com.leavemanagement.entity.LeaveBalance;
import com.leavemanagement.enums.LeaveStatus;
import com.leavemanagement.enums.LeaveType;
import com.leavemanagement.exception.InsufficientLeaveBalanceException;
import com.leavemanagement.exception.InvalidOperationException;
import com.leavemanagement.exception.ResourceNotFoundException;
import com.leavemanagement.repository.EmployeeRepository;
import com.leavemanagement.repository.LeaveApplicationRepository;
import com.leavemanagement.repository.LeaveBalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveApplicationRepository leaveApplicationRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public LeaveResponse applyLeave(String email, LeaveRequest request) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        LeaveType leaveType = LeaveType.valueOf(request.getLeaveType().toUpperCase());

        // Validate dates
        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new InvalidOperationException("Start date cannot be in the past");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new InvalidOperationException("End date cannot be before start date");
        }

        int numberOfDays = (int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        // Check leave balance
        int currentYear = Year.now().getValue();
        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeAndYear(employee.getId(), leaveType, currentYear)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found. Please contact admin."));

        if (balance.getRemainingLeaves() < numberOfDays) {
            throw new InsufficientLeaveBalanceException(
                    "Insufficient " + leaveType.name() + " leave balance. Available: " +
                            balance.getRemainingLeaves() + ", Requested: " + numberOfDays);
        }

        LeaveApplication application = LeaveApplication.builder()
                .employee(employee)
                .leaveType(leaveType)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .numberOfDays(numberOfDays)
                .reason(request.getReason())
                .status(LeaveStatus.APPLIED)
                .build();

        LeaveApplication saved = leaveApplicationRepository.save(application);
        return mapToResponse(saved);
    }

    @Transactional
    public LeaveResponse approveLeave(String managerEmail, Long leaveId, String remarks) {
        Employee manager = employeeRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        LeaveApplication leave = leaveApplicationRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave application not found with id: " + leaveId));

        // Verify this leave belongs to manager's subordinate
        if (leave.getEmployee().getManager() == null ||
                !leave.getEmployee().getManager().getId().equals(manager.getId())) {
            throw new InvalidOperationException("You can only approve leaves of your team members");
        }

        if (leave.getStatus() != LeaveStatus.APPLIED) {
            throw new InvalidOperationException("Only APPLIED leaves can be approved. Current status: " + leave.getStatus());
        }

        // Deduct leave balance
        int currentYear = Year.now().getValue();
        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeAndYear(leave.getEmployee().getId(), leave.getLeaveType(), currentYear)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found"));

        if (balance.getRemainingLeaves() < leave.getNumberOfDays()) {
            throw new InsufficientLeaveBalanceException("Employee has insufficient leave balance");
        }

        balance.setUsedLeaves(balance.getUsedLeaves() + leave.getNumberOfDays());
        leaveBalanceRepository.save(balance);

        leave.setStatus(LeaveStatus.APPROVED);
        leave.setManagerRemarks(remarks);
        leave.setActionDate(LocalDateTime.now());
        LeaveApplication updated = leaveApplicationRepository.save(leave);

        return mapToResponse(updated);
    }

    @Transactional
    public LeaveResponse rejectLeave(String managerEmail, Long leaveId, String remarks) {
        Employee manager = employeeRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        LeaveApplication leave = leaveApplicationRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave application not found with id: " + leaveId));

        if (leave.getEmployee().getManager() == null ||
                !leave.getEmployee().getManager().getId().equals(manager.getId())) {
            throw new InvalidOperationException("You can only reject leaves of your team members");
        }

        if (leave.getStatus() != LeaveStatus.APPLIED) {
            throw new InvalidOperationException("Only APPLIED leaves can be rejected. Current status: " + leave.getStatus());
        }

        leave.setStatus(LeaveStatus.REJECTED);
        leave.setManagerRemarks(remarks);
        leave.setActionDate(LocalDateTime.now());
        LeaveApplication updated = leaveApplicationRepository.save(leave);

        return mapToResponse(updated);
    }

    @Transactional
    public LeaveResponse cancelLeave(String email, Long leaveId) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        LeaveApplication leave = leaveApplicationRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave application not found with id: " + leaveId));

        if (!leave.getEmployee().getId().equals(employee.getId())) {
            throw new InvalidOperationException("You can only cancel your own leave applications");
        }

        if (leave.getStatus() != LeaveStatus.APPLIED) {
            throw new InvalidOperationException("Only APPLIED leaves can be cancelled. Current status: " + leave.getStatus());
        }

        leave.setStatus(LeaveStatus.CANCELLED);
        leave.setActionDate(LocalDateTime.now());
        LeaveApplication updated = leaveApplicationRepository.save(leave);

        return mapToResponse(updated);
    }

    @Transactional(readOnly = true)
    public List<LeaveResponse> getLeaveHistory(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        return leaveApplicationRepository.findByEmployeeId(employee.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LeaveBalanceResponse> getLeaveBalance(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        int currentYear = Year.now().getValue();
        return leaveBalanceRepository.findByEmployeeIdAndYear(employee.getId(), currentYear)
                .stream().map(this::mapToBalanceResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LeaveResponse> getPendingApprovals(String managerEmail) {
        Employee manager = employeeRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        return leaveApplicationRepository.findByEmployeeManagerIdAndStatus(manager.getId(), LeaveStatus.APPLIED)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LeaveResponse> getTeamLeaves(String managerEmail) {
        Employee manager = employeeRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        return leaveApplicationRepository.findByEmployeeManagerId(manager.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LeaveResponse> getAllLeaves() {
        return leaveApplicationRepository.findAll()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LeaveResponse> getLeavesByStatus(LeaveStatus status) {
        return leaveApplicationRepository.findByStatus(status)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private LeaveResponse mapToResponse(LeaveApplication leave) {
        return LeaveResponse.builder()
                .id(leave.getId())
                .employeeName(leave.getEmployee().getName())
                .leaveType(leave.getLeaveType().name())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .numberOfDays(leave.getNumberOfDays())
                .reason(leave.getReason())
                .status(leave.getStatus().name())
                .managerRemarks(leave.getManagerRemarks())
                .appliedDate(leave.getAppliedDate())
                .actionDate(leave.getActionDate())
                .build();
    }

    private LeaveBalanceResponse mapToBalanceResponse(LeaveBalance balance) {
        return LeaveBalanceResponse.builder()
                .leaveType(balance.getLeaveType().name())
                .totalLeaves(balance.getTotalLeaves())
                .usedLeaves(balance.getUsedLeaves())
                .remainingLeaves(balance.getRemainingLeaves())
                .build();
    }
}

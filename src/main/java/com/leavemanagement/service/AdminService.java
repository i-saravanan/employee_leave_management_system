package com.leavemanagement.service;

import com.leavemanagement.dto.request.RegisterRequest;
import com.leavemanagement.dto.response.EmployeeResponse;
import com.leavemanagement.entity.Employee;
import com.leavemanagement.entity.LeaveBalance;
import com.leavemanagement.enums.LeaveType;
import com.leavemanagement.enums.Role;
import com.leavemanagement.exception.InvalidOperationException;
import com.leavemanagement.exception.ResourceNotFoundException;
import com.leavemanagement.repository.EmployeeRepository;
import com.leavemanagement.repository.LeaveBalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final EmployeeRepository employeeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream().map(EmployeeService::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return EmployeeService.mapToResponse(employee);
    }

    @Transactional
    public EmployeeResponse createEmployee(RegisterRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new InvalidOperationException("Email already registered: " + request.getEmail());
        }

        Role role = Role.valueOf(request.getRole());

        Employee.EmployeeBuilder builder = Employee.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .department(request.getDepartment())
                .joiningDate(LocalDate.now())
                .active(true);

        if (request.getManagerId() != null) {
            Employee manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id: " + request.getManagerId()));
            builder.manager(manager);
        }

        Employee employee = employeeRepository.save(builder.build());

        // Initialize leave balances
        int currentYear = Year.now().getValue();
        for (LeaveType leaveType : LeaveType.values()) {
            LeaveBalance balance = LeaveBalance.builder()
                    .employee(employee)
                    .leaveType(leaveType)
                    .totalLeaves(leaveType.getDefaultDays())
                    .usedLeaves(0)
                    .year(currentYear)
                    .build();
            leaveBalanceRepository.save(balance);
        }

        return EmployeeService.mapToResponse(employee);
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long id, RegisterRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        if (request.getName() != null) employee.setName(request.getName());
        if (request.getEmail() != null) {
            if (!employee.getEmail().equals(request.getEmail()) &&
                    employeeRepository.existsByEmail(request.getEmail())) {
                throw new InvalidOperationException("Email already in use: " + request.getEmail());
            }
            employee.setEmail(request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            employee.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            employee.setRole(Role.valueOf(request.getRole()));
        }
        if (request.getDepartment() != null) {
            employee.setDepartment(request.getDepartment());
        }
        if (request.getManagerId() != null) {
            Employee manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id: " + request.getManagerId()));
            employee.setManager(manager);
        }

        Employee updated = employeeRepository.save(employee);
        return EmployeeService.mapToResponse(updated);
    }

    @Transactional
    public void deactivateEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        employee.setActive(false);
        employeeRepository.save(employee);
    }
}

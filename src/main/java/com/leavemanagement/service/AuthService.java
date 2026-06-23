package com.leavemanagement.service;

import com.leavemanagement.dto.request.LoginRequest;
import com.leavemanagement.dto.request.RegisterRequest;
import com.leavemanagement.dto.response.AuthResponse;
import com.leavemanagement.entity.Employee;
import com.leavemanagement.entity.LeaveBalance;
import com.leavemanagement.enums.LeaveType;
import com.leavemanagement.enums.Role;
import com.leavemanagement.exception.InvalidOperationException;
import com.leavemanagement.repository.EmployeeRepository;
import com.leavemanagement.repository.LeaveBalanceRepository;
import com.leavemanagement.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
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
                    .orElseThrow(() -> new InvalidOperationException("Manager not found with id: " + request.getManagerId()));
            builder.manager(manager);
        }

        Employee employee = employeeRepository.save(builder.build());

        // Initialize leave balances for current year
        initializeLeaveBalances(employee);

        // Auto-login after registration
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        String token = jwtTokenProvider.generateToken(authentication);

        return AuthResponse.builder()
                .token(token)
                .email(employee.getEmail())
                .role(employee.getRole().name())
                .name(employee.getName())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String token = jwtTokenProvider.generateToken(authentication);

        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidOperationException("Employee not found"));

        return AuthResponse.builder()
                .token(token)
                .email(employee.getEmail())
                .role(employee.getRole().name())
                .name(employee.getName())
                .build();
    }

    private void initializeLeaveBalances(Employee employee) {
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
    }
}

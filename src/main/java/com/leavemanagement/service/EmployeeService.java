package com.leavemanagement.service;

import com.leavemanagement.dto.response.EmployeeResponse;
import com.leavemanagement.entity.Employee;
import com.leavemanagement.exception.ResourceNotFoundException;
import com.leavemanagement.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public EmployeeResponse getProfile(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + email));
        return mapToResponse(employee);
    }

    @Transactional(readOnly = true)
    public Employee getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + email));
    }

    public static EmployeeResponse mapToResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .name(employee.getName())
                .email(employee.getEmail())
                .role(employee.getRole().name())
                .department(employee.getDepartment())
                .managerName(employee.getManager() != null ? employee.getManager().getName() : null)
                .joiningDate(employee.getJoiningDate())
                .active(employee.isActive())
                .build();
    }
}

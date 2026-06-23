package com.leavemanagement.repository;

import com.leavemanagement.entity.Employee;
import com.leavemanagement.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Employee> findByManagerId(Long managerId);
    List<Employee> findByRole(Role role);
    List<Employee> findByActiveTrue();
}

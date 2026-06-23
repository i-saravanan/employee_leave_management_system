package com.leavemanagement.repository;

import com.leavemanagement.entity.LeaveApplication;
import com.leavemanagement.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {
    List<LeaveApplication> findByEmployeeId(Long employeeId);
    List<LeaveApplication> findByEmployeeManagerIdAndStatus(Long managerId, LeaveStatus status);
    List<LeaveApplication> findByEmployeeManagerId(Long managerId);
    List<LeaveApplication> findByStatus(LeaveStatus status);
}

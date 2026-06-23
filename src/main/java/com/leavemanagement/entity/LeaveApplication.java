package com.leavemanagement.entity;

import com.leavemanagement.enums.LeaveStatus;
import com.leavemanagement.enums.LeaveType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveType leaveType;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private int numberOfDays;

    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus status;

    private String managerRemarks;

    @Column(nullable = false, updatable = false)
    private LocalDateTime appliedDate;

    private LocalDateTime actionDate;

    @PrePersist
    protected void onCreate() {
        this.appliedDate = LocalDateTime.now();
        if (this.status == null) {
            this.status = LeaveStatus.APPLIED;
        }
    }
}

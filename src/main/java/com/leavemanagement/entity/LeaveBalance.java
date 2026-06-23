package com.leavemanagement.entity;

import com.leavemanagement.enums.LeaveType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_balances", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id", "leave_type", "year"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type", nullable = false)
    private LeaveType leaveType;

    @Column(nullable = false)
    private int totalLeaves;

    @Column(nullable = false)
    private int usedLeaves;

    @Column(nullable = false)
    private int year;

    public int getRemainingLeaves() {
        return totalLeaves - usedLeaves;
    }
}

package com.leavemanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceResponse {
    private String leaveType;
    private int totalLeaves;
    private int usedLeaves;
    private int remainingLeaves;
}

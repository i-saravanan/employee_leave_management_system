package com.leavemanagement.enums;

public enum LeaveType {
    CASUAL(12),
    SICK(10),
    EARNED(15);

    private final int defaultDays;

    LeaveType(int defaultDays) {
        this.defaultDays = defaultDays;
    }

    public int getDefaultDays() {
        return defaultDays;
    }
}

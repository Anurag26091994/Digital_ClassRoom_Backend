package com.digital.events;

import java.time.LocalDate;

public record LeaveApprovalEvent(
        Long leaveId,
        LocalDate fromDate,
        LocalDate toDate,
        String status,
        Long userId
) {
}

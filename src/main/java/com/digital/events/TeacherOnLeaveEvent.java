package com.digital.events;

import java.time.LocalDate;
import java.util.List;

public record TeacherOnLeaveEvent(
        Long userId,
        Long teacherId,
        Long leaveId,
        LocalDate fromDate,
        LocalDate toDate,
        List<Long> schoolClassIds
) {
}

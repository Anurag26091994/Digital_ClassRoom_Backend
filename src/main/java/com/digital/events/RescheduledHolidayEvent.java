package com.digital.events;

import java.time.LocalDate;
import java.util.List;

public record RescheduledHolidayEvent(
        Long holidayId,
        String holidayName,
        LocalDate fromDate,
        LocalDate rescheduledDate,
        List<Long> studentIds,
        List<Long> teacherIds
) {}

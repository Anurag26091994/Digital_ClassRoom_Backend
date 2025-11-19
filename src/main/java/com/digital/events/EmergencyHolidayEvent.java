package com.digital.events;

import java.time.LocalDate;
import java.util.List;

public record EmergencyHolidayEvent(
        Long holidayId,
        LocalDate date,
        List<Long> studentIds,
        List<Long> teacherIds
) {}

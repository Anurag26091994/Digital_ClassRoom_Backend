package com.digital.events;

import java.time.LocalDate;
import java.util.List;

public record RescheduledEvent(
        Long eventId,
        String eventName,
        LocalDate fromDate, // old date
        LocalDate toDate,  // reschedule date
        List<Long> studentIds,
        List<Long> teacherIds
) {}

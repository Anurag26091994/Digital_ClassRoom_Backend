package com.digital.dto;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class HolidayDto {

    private Long holidayId;

    private Long calendarId; // (FK → AcademicCalendar)

    private String academicYear;

    private LocalDate holidayDate; // (Date)

    private String holidayName; // (e.g., Independence Day)

    private Boolean isEmergency; // (Boolean, default false)

    private Boolean rescheduleRequired; // (Boolean, default false)

    private LocalDate rescheduledDate;

    private LocalDate timetableRescheduleDate; // in case of emergency holiday

    private LocalDate eventRescheduleDate; // in case of emergency holiday
}

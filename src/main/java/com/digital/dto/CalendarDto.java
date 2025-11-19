package com.digital.dto;

import com.digital.enums.Role;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CalendarDto {

    private Long calendarId;

    private String academicYear;

    private LocalDate startDate;

    private LocalDate endDate;

    private Role createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

package com.digital.dto;

import com.digital.enums.AttendanceStatus;
import com.digital.enums.MarkBy;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@Builder
public class GeneratePdfReportResponse {

    private String rollNo;

    private String fullName;

    private LocalDate date;

    private Long sessionId;

    private String sessionTopic;

    private LocalTime joinTime;

    private LocalTime exitTime;

    private Long durationMinutes;

    private Long teacherId;

    private String teacherName;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus attendanceStatus;

    @Enumerated(EnumType.STRING)
    private MarkBy markBy;
}

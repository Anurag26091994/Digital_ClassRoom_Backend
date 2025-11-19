package com.digital.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.List;

@Data
@Builder
public class ReportCardViewDto {
    private Long studentRegId;
    private String schoolName;
    private String schoolAddress;
    private String term;
    private String studentName;
    private String className;
    private String rollNo;
    private String admissionNo;
    private String academicYear;
    private List<ReportCardSubjectDto> subjects;
    private BigDecimal totalMarks;
    private BigDecimal totalObtained;
    private String overallPercentage;
    private String overallGrade;
    private String resultStatus;
    private String attendancePercent;
    private Integer leavesTaken;
    private String teacherRemarks;
    private LocalDate generatedDate;
}

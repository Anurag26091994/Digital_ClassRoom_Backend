package com.digital.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ReportCardSubjectDto {
    private Long subjectId;
    private String subjectName;
    private BigDecimal totalMarks;
    private BigDecimal obtainedMarks;
    private String percentage; // "78" or "78.00"
    private String grade;
    private String remarks;

}

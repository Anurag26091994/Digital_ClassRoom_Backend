package com.digital.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
@Data
@Builder
public class SubjectResultResponse {
    private Long subjectId;
    private String subjectName;
    private BigDecimal obtainedMarks;
    private BigDecimal totalMarks;
    private BigDecimal percentage;
    private String grade;
    private String remarks;
}

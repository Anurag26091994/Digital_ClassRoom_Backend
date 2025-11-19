package com.digital.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ReportCardResponse {
    private Long reportCardId;
    private Long studentId;
    private String term;
    private BigDecimal totalMarks;
    private BigDecimal obtainedMarks;
    private BigDecimal percentage;
    private String grade;
    private String remarks;
    private LocalDateTime generatedAt;

    private List<SubjectReportDto> subjects;

}

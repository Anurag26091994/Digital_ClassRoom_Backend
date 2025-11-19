package com.digital.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ResultResponse {
    private Long resultId;
    private Long examId;
    private Long studentId;
    private BigDecimal obtainedMarks;
    private BigDecimal percentage;
    private String grade;
    private String status;         // PUBLISHED / DRAFT
    private LocalDateTime publishedAt;

    // Optional additional info for frontend convenience
    private String studentName;
    private String examName;
    private String subjectName;
}

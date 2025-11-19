package com.digital.dto;

import com.digital.enums.ExamType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ExamRequest {
    private Long classId;
    private Long sectionId;
    private Long subjectId;
    private Long teacherId;
    private ExamType examType;
    private String examName;
    private String term;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;
    private BigDecimal totalMarks;
}

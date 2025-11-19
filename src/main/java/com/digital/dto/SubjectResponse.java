package com.digital.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SubjectResponse {
    private Long subjectId;
    private String subjectName;
    private String subjectCode;
    private Long classId;
    private String className;
    private Long teacherId;
    private String teacherName;
    private String description;
    private BigDecimal maxMarks;
    private Boolean isActive;
}

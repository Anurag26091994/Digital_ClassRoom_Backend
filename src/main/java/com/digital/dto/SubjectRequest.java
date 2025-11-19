package com.digital.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubjectRequest {
    private String subjectName;
    private String subjectCode;
    private Long classId;
    private Long teacherId;
    private String description;
    private BigDecimal maxMarks;
    private Boolean isActive;
}

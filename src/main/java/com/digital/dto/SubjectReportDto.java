package com.digital.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubjectReportDto {
    private Long subjectId;
    private String subjectName;
    private Double obtainedMarks; // Marks scored by the student
    private String grade;         // Grade for this subject
}

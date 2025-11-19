package com.digital.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@Builder
public class UpcomingExamResponse {
    private Long examId;
    private Long classId;
    private String className;
    private Long sectionId;
    private String sectionName;
    private Long subjectId;
    private String subjectName;
    private Long teacherId;
    private String teacherName;
    private String examType;
    private String examName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;
    private BigDecimal totalMarks;
}

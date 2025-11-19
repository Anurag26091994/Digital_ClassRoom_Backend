package com.digital.dto;

import com.digital.enums.ExamType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ExamResponse {
    private Long examId;
    private Long classId;
    private String className;
    private Long sectionId;
    private String sectionName;
    private Long subjectId;
    private String subjectName;
    private Long teacherId;
    private String teacherName;
    private ExamType examType;
    private String examName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;
    private BigDecimal totalMarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

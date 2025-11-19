package com.digital.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReportCardRequest {
//    private List<Long> examIds;
//    private Long classId;       // mandatory
//    private Long sectionId;     // mandatory
//    private Long studentId;     // optional: if null, generate for all students
//    private List<Long> subjectId; // optional: filter by subjects
//    private String term;
//    private Long requesterId; // optional, for tracking
private Long classId;
    private Long sectionId;
    private Long subjectId;
    private String term;
    private List<Long> examIds;
}


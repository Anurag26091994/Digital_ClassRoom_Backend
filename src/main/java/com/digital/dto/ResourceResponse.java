package com.digital.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;
import java.time.LocalDateTime;

@Getter
@Setter
public class ResourceResponse {
    private Long resourceId;
    private String title;
    private String description;
    private Blob fileUrl;
    private Long classId;
    private Long sectionId;
    private Long subjectId;
    private Long uploadedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

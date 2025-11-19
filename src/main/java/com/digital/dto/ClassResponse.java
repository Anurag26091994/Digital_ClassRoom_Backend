package com.digital.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassResponse {
    private Long classId;
    private String className;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<SectionResponse> sections;
}

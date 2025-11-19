package com.digital.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SectionResponse {
    private Long sectionId;
    private String sectionName;
    private Integer capacity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}

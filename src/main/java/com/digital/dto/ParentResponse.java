package com.digital.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParentResponse {
    private Long parentId;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String relationship;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<StudentResponse> students;
}

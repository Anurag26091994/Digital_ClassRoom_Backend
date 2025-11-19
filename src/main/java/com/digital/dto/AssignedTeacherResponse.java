package com.digital.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignedTeacherResponse {
    private Long id;
    private Long classId;
    private List<String> className;
    private Long sectionId;
    private List<String> sectionName;
    private Long teacherId;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime assignedAt;
}

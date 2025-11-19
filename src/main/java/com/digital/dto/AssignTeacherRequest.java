package com.digital.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignTeacherRequest {
    @NotNull(message = "Teacher ID is required")
    private Long teacherId;

}

package com.digital.dto;

import com.digital.entity.SchoolClass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SectionRequest {
    private SchoolClass schoolClass;

    @NotBlank(message = "Section name is required")
    private String sectionName;

    @NotNull(message = "capacity is required")
    private Integer capacity;
}

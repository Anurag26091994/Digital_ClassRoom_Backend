package com.digital.dto;

import com.digital.enums.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ManagerStatusDto {

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private Status status;
}

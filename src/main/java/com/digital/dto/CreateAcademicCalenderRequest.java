package com.digital.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CreateAcademicCalenderRequest {

    @NotBlank(message = "Academic calender cannot be null")
    private String academicYear; // (e.g., "2025-26")

    @NotNull(message = "Start date cannot be null")
    @FutureOrPresent(message = "Start date must be from today or future.")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    @FutureOrPresent(message = "End date must be from present or future")
    private LocalDate endDate;

    @AssertTrue(message = "End date must be after start date")
    public boolean isEndDateAfterStartDate(){
        if(startDate == null | endDate == null)
            return true;

        return endDate.isAfter(startDate);
    }
}

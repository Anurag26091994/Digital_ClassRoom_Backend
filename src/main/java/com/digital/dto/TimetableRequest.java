package com.digital.dto;

import com.digital.entity.SchoolClass;
import com.digital.entity.Section;
import com.digital.entity.Subject;
import com.digital.entity.Teacher;
import com.digital.enums.DayOfWeek;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TimetableRequest {

    private SchoolClass schoolClass;

    private Section section;

    private Subject subject;

    private Teacher teacher;

    @NotNull(message = "Day of week is required")
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @FutureOrPresent(message = "Only Present or Future date is allowed")
    private LocalDate date;

    @NotBlank
    @Size(min = 2, max = 50, message = "Topic must be between 2 to 50 characters")
    private String topic;

    @Size(max = 300, message = "Only 300 characters are allowed in description")
    private String description;
}

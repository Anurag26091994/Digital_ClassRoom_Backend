package com.digital.dto;

import com.digital.entity.Admin;
import com.digital.entity.Teacher;
import com.digital.enums.LeaveType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class MakeLeaveRequest {

    @NotNull(message = "Leave type cannot be null")
    @Enumerated(EnumType.STRING)
    private LeaveType leaveType; // (ENUM: Sick, Casual, Exam, Personal, Emergency)

    @NotNull(message = "From date cannot be null")
    private LocalDate fromDate;

    @NotNull(message = "To date  cannot be null")
    private LocalDate toDate;

    @NotBlank(message = "Reason cannot be blank.")
    private String reason; // (Text)

    private Teacher approvedByTeacher; // If STUDENT is making a leave request then enable this field.
                                       // If TEACHER is making a leave request then disable it.
    private Admin approvedByAdmin;
}

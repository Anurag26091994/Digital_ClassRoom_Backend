package com.digital.dto;

import com.digital.enums.LeaveRequestStatus;
import com.digital.enums.LeaveType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class LeaveRequestDto {

    private Long leaveId;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType; // (ENUM: Sick, Casual, Exam, Personal, Emergency)

    private LocalDate fromDate;

    private LocalDate toDate;

    private String reason; // (Text)

    @Enumerated(EnumType.STRING)
    private LeaveRequestStatus status; // (ENUM: Pending, Approved, Rejected)

    private LocalDate appliedOn; // (DateTime)

    private Long approvedByTeacher;

    private String classTeacherName;

    private Long approvedByAdmin;

    private LocalDate approvalDate; // (Nullable DateTime)

    private String remarks; // (Optional String)
}

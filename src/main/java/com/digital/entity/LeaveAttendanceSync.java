package com.digital.entity;

import com.digital.enums.AttendanceStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
public class LeaveAttendanceSync {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long syncId; // (PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "Leave request cannot be null")
    @JoinColumn(name = "leaveId")
    private LeaveRequest leaveRequest; // (FK → LeaveRequest)

    @NotNull(message = "Date cannot be null")
    @Column(nullable = false)
    private LocalDate date; // (Date)

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @NotNull(message = "Attendance cannot be null")
    @JoinColumn(name = "attendanceId")
    private Attendance attendance; // (FK → Attendance, Nullable)

    @Enumerated(EnumType.STRING)
    private AttendanceStatus attendanceStatus; // (ENUM: Leave, Absent, Present)
}

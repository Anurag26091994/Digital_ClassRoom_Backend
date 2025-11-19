package com.digital.entity;

import com.digital.enums.LeaveRequestStatus;
import com.digital.enums.LeaveType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long leaveId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user; // (FK → User → Student/Teacher)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveType leaveType; // (ENUM: Sick, Casual, Exam, Personal, Emergency)

    @Column(nullable = false)
    private LocalDate fromDate;

    @Column(nullable = false)
    private LocalDate toDate;

    @Column(nullable = false)
    private String reason; // (Text)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveRequestStatus status; // (ENUM: Pending, Approved, Rejected)

    @Column(nullable = false)
    private LocalDate appliedOn; // (DateTime)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacherId")
    private Teacher approvedByTeacher; // (FK → Teacher/Admin, Nullable)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approvedByAdmin")
    private Admin approvedByAdmin;

    private LocalDate approvalDate; // (Nullable DateTime)

    private String remarks; // (Optional String)
}

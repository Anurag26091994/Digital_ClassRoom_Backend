package com.digital.entity;

import com.digital.enums.SubmissionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long submissionId;


    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    private LocalDateTime submittedAt;


    @Column(columnDefinition = "TEXT")
    private String answers; // JSON string: [{"questionId":...,"answer":...}, ...]


    private BigDecimal obtainedMarks;


    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;


    private Long evaluatedBy;
    private LocalDateTime evaluatedAt;
}

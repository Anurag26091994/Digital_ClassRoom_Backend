package com.digital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "report_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportCardId;


    // Link to Student entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Optional: link to Subject entity (if report card is subject-wise)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false)
    private Result result; // ✅ this is required

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private ExamSubmission submission;


    private String term;
    private BigDecimal totalMarks;
    private BigDecimal obtainedMarks;
    private BigDecimal percentage;
    private String grade;
    private String remarks;
    private LocalDateTime generatedAt;


    @PrePersist
    public void prePersist() { generatedAt = LocalDateTime.now(); }
}

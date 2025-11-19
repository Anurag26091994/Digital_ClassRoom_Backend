package com.digital.entity;

import com.digital.enums.ResultStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "results", uniqueConstraints = @UniqueConstraint(columnNames = {"examId","studentId"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;


    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "studentRegId")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "exam_id", referencedColumnName = "examId")
    private Exam exam;

    private BigDecimal obtainedMarks;

    private BigDecimal percentage;

    private String grade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ResultStatus status;

    private LocalDateTime publishedAt;
}

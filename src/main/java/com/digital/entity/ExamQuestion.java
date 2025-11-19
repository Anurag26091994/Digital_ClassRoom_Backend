package com.digital.entity;

import com.digital.enums.QuestionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "exam_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;


    private Long examId;


    @Column(columnDefinition = "TEXT")
    private String questionText;


    @Enumerated(EnumType.STRING)
    private QuestionType questionType;


    @Column(columnDefinition = "TEXT")
    private String options; // JSON string for MCQ options


    @Column(columnDefinition = "TEXT")
    private String correctAnswer; // JSON string or plain text


    private BigDecimal marks;
}

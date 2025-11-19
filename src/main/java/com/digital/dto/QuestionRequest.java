package com.digital.dto;

import com.digital.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionRequest {
    private String questionText;
    private QuestionType questionType;
    private List<String> options; // for MCQ
    private String correctAnswer; // for MCQ (string or JSON)
    private BigDecimal marks;
}

package com.digital.servicei;

import com.digital.dto.QuestionRequest;

import java.util.List;

public interface ExamQuestionService {
    void addQuestions(Long examId, List<QuestionRequest> questions, Long teacherId);
}

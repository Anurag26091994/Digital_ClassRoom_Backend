package com.digital.serviceimpl;

import com.digital.dto.QuestionRequest;
import com.digital.entity.Exam;
import com.digital.entity.ExamQuestion;
import com.digital.exception.ExamNotFoundException;
import com.digital.repository.ExamQuestionRepository;
import com.digital.repository.ExamRepository;
import com.digital.servicei.ExamQuestionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ExamQuestionServiceImpl implements ExamQuestionService {
    private final ExamRepository examRepository;
    private final ExamQuestionRepository questionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public void addQuestions(Long examId, List<QuestionRequest> questions, Long teacherId) {
        Exam exam = examRepository.findById(examId).orElseThrow(() -> new ExamNotFoundException("Exam not found: " + examId));
        // optional: check teacherId equals exam.teacherId; security layer should handle but double-check if desired.
        if (!exam.getTeacher().getId().equals(teacherId)) {
            throw new SecurityException("Teacher not assigned to this exam");
        }


        List<ExamQuestion> ents = new ArrayList<>();
        for (QuestionRequest q : questions) {
            try {
                String opts = q.getOptions() == null ? null : objectMapper.writeValueAsString(q.getOptions());
                ExamQuestion eq = ExamQuestion.builder()
                        .examId(examId)
                        .questionText(q.getQuestionText())
                        .questionType(q.getQuestionType())
                        .options(opts)
                        .correctAnswer(q.getCorrectAnswer())
                        .marks(q.getMarks())
                        .build();
                ents.add(eq);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to serialize question options", ex);
            }
        }
        questionRepository.saveAll(ents);
    }
}

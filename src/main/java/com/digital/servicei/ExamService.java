package com.digital.servicei;

import com.digital.dto.ExamRequest;
import com.digital.dto.ExamResponse;
import com.digital.dto.QuestionRequest;
import com.digital.dto.ReportCardRequest;

import java.util.List;
import java.util.Map;

public interface ExamService {

    ExamResponse createExam(ExamRequest request);

    ExamResponse updateExam(Long examId, ExamRequest request);

    void deleteExam(Long examId);

    List<ExamResponse> getExams(Long classId, Long sectionId, Long subjectId, Long teacherId);

    void generateReportCards(ReportCardRequest request);

   // List<ExamResponse> getExamsForStudent(Long studentId);  // from student APIs

    // ⬇️ TEACHER specific
    void addQuestions(Long examId, List<QuestionRequest> questions);

    //void evaluateSubmission(Long examId, Long studentId, Long teacherId, Double obtainedMarks);
    void evaluateSubmission(Long examId, Long studentId, Long teacherId, Map<Long, Double> subjectMarks);
    List<?> getResultsByTeacher(Long teacherId);
}

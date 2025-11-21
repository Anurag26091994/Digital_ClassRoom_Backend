package com.digital.controller;

import com.digital.dto.QuestionRequest;
import com.digital.dto.SubmissionResponse;
import com.digital.servicei.ExamQuestionService;
import com.digital.servicei.ExamService;
//import com.digital.servicei.SubmissionService;
import com.digital.servicei.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
@CrossOrigin("*")
public class TeacherExamController {

    private final ExamService examService;
    private final SubmissionService submissionService;
    private final ExamQuestionService examQuestionService;

    // 1. Upload Questions for Online Exams
    @PostMapping("/exams/{examId}/questions")
    public ResponseEntity<String> addQuestions(
            @PathVariable Long examId,
            @RequestParam Long teacherId,   // teacher sending request
            @RequestBody List<QuestionRequest> questions) {

        examQuestionService.addQuestions(examId, questions, teacherId);
        return ResponseEntity.ok("Questions added successfully for examId: " + examId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @PostMapping("/exams/{examId}/evaluate/{studentId}")
    public ResponseEntity<String> evaluateSubmission(
            @PathVariable Long examId,
            @PathVariable Long studentId,
            @RequestParam Long teacherId,
            @RequestBody Map<Long, Double> subjectMarks) {

        examService.evaluateSubmission(examId, studentId, teacherId, subjectMarks);

        return ResponseEntity.ok("Submission evaluated successfully for studentId: " + studentId);
    }

     //3. View all Submissions for an Exam
    @GetMapping("/exams/{examId}/submissions")
    public ResponseEntity<List<SubmissionResponse>> getSubmissions(@PathVariable Long examId) {
        List<SubmissionResponse> submissions = submissionService.getSubmissionsForExam(examId, null);
        return ResponseEntity.ok(submissions);
    }

    // 4. Fetch Results of Exams handled by Teacher
    @GetMapping("/results")
    public ResponseEntity<?> getTeacherResults(@RequestParam Long teacherId) {
        return ResponseEntity.ok(examService.getResultsByTeacher(teacherId));
    }
}

package com.digital.controller;

import com.digital.dto.ExamRequest;
import com.digital.dto.ExamResponse;
import com.digital.dto.ReportCardRequest;
import com.digital.dto.ReportCardResponse;
import com.digital.entity.ReportCard;
import com.digital.servicei.ExamService;
import com.digital.servicei.ReportCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/exams")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdminExamController {

    private final ExamService examService;
    private final ReportCardService reportCardService;

    @PostMapping
    public ResponseEntity<ExamResponse> createExam(@RequestBody ExamRequest request) {
        return ResponseEntity.ok(examService.createExam(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamResponse> updateExam(@PathVariable Long id, @RequestBody ExamRequest request) {
        return ResponseEntity.ok(examService.updateExam(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.ok("Exam deleted successfully");
    }


    @GetMapping
    public ResponseEntity<List<ExamResponse>> getExams(
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long sectionId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long teacherId
    ) {
        return ResponseEntity.ok(examService.getExams(classId, sectionId, subjectId, teacherId));
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @PostMapping("/generate")
    public ResponseEntity<?> generateReportCards(@RequestBody ReportCardRequest request) {
        reportCardService.generateReportCards(
                request.getClassId(),
                request.getSectionId(),
                request.getSubjectId(),
                request.getTerm(),
                request.getExamIds()
        );

        return ResponseEntity.ok(Map.of("message", "Report card generated successfully"));
    }



}

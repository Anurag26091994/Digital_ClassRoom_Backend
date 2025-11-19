package com.digital.serviceimpl;

import com.digital.dto.ReportCardSubjectDto;
import com.digital.dto.SubmissionRequest;
import com.digital.dto.SubmissionResponse;
import com.digital.entity.Exam;
import com.digital.entity.ExamSubmission;
import com.digital.entity.ReportCard;
import com.digital.entity.Student;
import com.digital.repository.ExamRepository;
import com.digital.repository.ReportCardRepository;
import com.digital.repository.SubmissionRepository;
import com.digital.repository.StudentRepository;
import com.digital.servicei.SubmissionService;
import com.digital.enums.SubmissionStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final ExamRepository examRepository;
    private final StudentRepository studentRepository;
    private final ReportCardRepository reportCardRepository;
    @Autowired
    public SubmissionServiceImpl(SubmissionRepository submissionRepository,
                                 ExamRepository examRepository,
                                 StudentRepository studentRepository,
                                 ReportCardRepository reportCardRepository) {
        this.submissionRepository = submissionRepository;
        this.examRepository = examRepository;
        this.studentRepository = studentRepository;
        this.reportCardRepository = reportCardRepository;
    }


    @Override
    public List<SubmissionResponse> getSubmissionsForExam(Long examId, Long teacherId) {

        List<ExamSubmission> submissions = submissionRepository.findByExam_ExamId(examId);

        return submissions.stream().map(sub -> {

            // Fetch subject-wise marks if evaluated
            List<ReportCardSubjectDto> subjectMarks = new ArrayList<>();
            if (sub.getStatus() == SubmissionStatus.EVALUATED) {
                List<ReportCard> reportCards = reportCardRepository.findBySubmission_SubmissionId(sub.getSubmissionId());
                subjectMarks = reportCards.stream()
                        .map(rc -> ReportCardSubjectDto.builder()
                                .subjectId(rc.getSubject().getSubjectId())
                                .subjectName(rc.getSubject().getSubjectName())
                                .totalMarks(rc.getTotalMarks())
                                .obtainedMarks(rc.getObtainedMarks())
                                .percentage(rc.getPercentage().toString())
                                .grade(rc.getGrade())
                                .remarks(rc.getRemarks())
                                .build())
                        .collect(Collectors.toList());
            }

            return SubmissionResponse.builder()
                    .examId(sub.getExam().getExamId())
                    .studentId(sub.getStudent().getStudentRegId())
                    .answers(sub.getAnswers())
                    .submittedAt(sub.getSubmittedAt())
                    .assignmentStatus(sub.getStatus() != null ? sub.getStatus() : SubmissionStatus.PENDING)
                    .obtainedMarks(sub.getStatus() == SubmissionStatus.EVALUATED ? sub.getObtainedMarks() : null)
                    .subjectMarks(subjectMarks)
                    .build();

        }).collect(Collectors.toList());
    }


    @Override
    public void submitExam(Long examId, Long studentId, SubmissionRequest request) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String answersJson = objectMapper.writeValueAsString(request.getAnswers());

            ExamSubmission submission = new ExamSubmission();
            submission.setExam(exam);
            submission.setStudent(student);
            submission.setAnswers(answersJson);
            submission.setSubmittedAt(LocalDateTime.now());
            submission.setStatus(SubmissionStatus.PENDING);

            submissionRepository.save(submission);
        } catch (Exception e) {
            throw new RuntimeException("Error saving submission JSON", e);
        }
    }

    @Override
    public SubmissionResponse getSubmission(Long examId, Long studentId) {
        ExamSubmission submission = submissionRepository
                .findByExam_ExamIdAndStudent_StudentRegId(examId, studentId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        return SubmissionResponse.builder()
                .examId(submission.getExam().getExamId())
                .studentId(submission.getStudent().getStudentRegId())
                .answers(submission.getAnswers())
                .submittedAt(submission.getSubmittedAt())
                .obtainedMarks(submission.getObtainedMarks())
                .assignmentStatus(submission.getStatus() != null ? submission.getStatus() : SubmissionStatus.PENDING)
                .build();
    }

}

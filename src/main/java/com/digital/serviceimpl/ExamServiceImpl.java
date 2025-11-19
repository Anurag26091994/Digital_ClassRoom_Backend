package com.digital.serviceimpl;

import com.digital.dto.*;
import com.digital.entity.*;
import com.digital.enums.ResultStatus;
import com.digital.enums.SubmissionStatus;
import com.digital.events.ExamCreatedEvent;
import com.digital.events.ResultPublishedEvent;
import com.digital.exception.ResourceNotFoundException;
import com.digital.repository.*;
import com.digital.servicei.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final ClassRepository classRepository;
    private final SectionRepository sectionRepository;
    private final SubjectRepository subjectRepository;
    private final ExamSubmissionRepository examSubmissionRepository;
    private final ResultRepository resultRepository;
    private final SubmissionRepository submissionRepository;
    private final ReportCardRepository reportCardRepository;


    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Override
    public ExamResponse createExam(ExamRequest request) {
        // 1. Fetch related entities
        var schoolClass = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found"));
        var section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));
        var subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        var teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        // 2. Build Exam entity
        Exam exam = Exam.builder()
                .examName(request.getExamName())
                .schoolClass(schoolClass)
                .section(section)
                .subject(subject)
                .teacher(teacher)
                .examType(request.getExamType())
                .term(request.getTerm())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .duration(request.getDuration())
                .totalMarks(request.getTotalMarks())
                .build();

        // 3. Save to DB
        Exam savedExam = examRepository.save(exam);

        // 4. Get all student & teacher user IDs from this class
        List<Long> studentIds = studentRepository.findAllBySchoolClass_ClassIdAndSection_SectionId(
                        schoolClass.getClassId(), section.getSectionId())
                .stream().map(student -> student.getUser().getUserId()).toList();

        List<Long> teacherIds = teacherRepository.findAllByAssignedClass_ClassId(
                        schoolClass.getClassId())
                .stream().map(t -> t.getUser().getUserId()).toList();

        // 5. Publish the event
        publisher.publishEvent(new ExamCreatedEvent(
                savedExam.getExamId(),
                studentIds,
                teacherIds
        ));

        // 6. Convert to DTO
        return convertToResponse(savedExam);
    }



    @Override
    public ExamResponse updateExam(Long examId, ExamRequest request) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id " + examId));

        var schoolClass = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found"));
        var section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));
        var subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        var teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        exam.setExamName(request.getExamName());
        exam.setSchoolClass(schoolClass);
        exam.setSection(section);
        exam.setSubject(subject);
        exam.setTeacher(teacher);
        exam.setExamType(request.getExamType());
        exam.setStartTime(request.getStartTime());
        exam.setEndTime(request.getEndTime());
        exam.setDuration(request.getDuration().intValue());
        exam.setTotalMarks(request.getTotalMarks());

        return convertToResponse(examRepository.save(exam));
    }

    @Override
    public void deleteExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id " + examId));

        if (exam.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot delete an exam that has already started");
        }
        examRepository.delete(exam);
    }

    @Override
    public List<ExamResponse> getExams(Long classId, Long sectionId, Long subjectId, Long teacherId) {
        List<Exam> exams = examRepository.findAll();

        return exams.stream()
                .filter(e -> classId == null || e.getSchoolClass().getClassId().equals(classId))
                .filter(e -> sectionId == null || e.getSection().getSectionId().equals(sectionId))
                .filter(e -> subjectId == null || e.getSubject().getSubjectId().equals(subjectId))
                .filter(e -> teacherId == null || e.getTeacher().getId().equals(teacherId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void generateReportCards(ReportCardRequest request) {
        System.out.println("Report cards generated for: " + request.getExamIds());
    }

    private ExamResponse convertToResponse(Exam e) {
        return ExamResponse.builder()
                .examId(e.getExamId())
                .examName(e.getExamName())
                .classId(e.getSchoolClass().getClassId())
                .className(e.getSchoolClass().getClassName())
                .sectionId(e.getSection().getSectionId())
                .sectionName(e.getSection().getSectionName())
                .subjectId(e.getSubject().getSubjectId())
                .subjectName(e.getSubject().getSubjectName())
                .teacherId(e.getTeacher().getId())
                .teacherName(e.getTeacher().getFirstName() + " " + e.getTeacher().getLastName())
                .examType(e.getExamType())
                .startTime(e.getStartTime())
                .endTime(e.getEndTime())
                .duration(e.getDuration())
                .totalMarks(e.getTotalMarks())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    @Override
    public void addQuestions(Long examId, List<QuestionRequest> questions) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id " + examId));
        System.out.println("Added " + questions.size() + " questions to exam " + examId);
    }

    @Override
    @Transactional
    public void evaluateSubmission(Long examId, Long studentId, Long teacherId, Map<Long, Double> subjectMarks) {

        //Fetch student's exam submission
        ExamSubmission submission = submissionRepository
                .findByExam_ExamIdAndStudent_StudentRegId(examId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Submission not found for studentId: " + studentId));

        BigDecimal totalObtained = BigDecimal.ZERO;

        Result result = resultRepository.findByStudent_StudentRegIdAndExam_ExamId(studentId, examId)
                .orElseGet(() -> {
                    Student student = studentRepository.findById(studentId)
                            .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

                    Exam exam = examRepository.findById(examId)
                            .orElseThrow(() -> new ResourceNotFoundException("Exam not found: " + examId));

                    Result newResult = Result.builder()
                            .student(student)
                            .exam(exam)
                            .status(ResultStatus.PENDING) // default status
                            .build();

                    return resultRepository.saveAndFlush(newResult);
                });

        // Subject-wise evaluation and report card creation
        for (Map.Entry<Long, Double> entry : subjectMarks.entrySet()) {
            Long subjectId = entry.getKey();
            Double marks = entry.getValue();

            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + subjectId));

            BigDecimal obtained = BigDecimal.valueOf(marks);
            totalObtained = totalObtained.add(obtained);

            // Create or update ReportCard entry
            ReportCard reportCard = reportCardRepository
                    .findBySubmission_SubmissionIdAndSubject_SubjectId(submission.getSubmissionId(), subjectId)
                    .orElse(ReportCard.builder()
                            .submission(submission)
                            .subject(subject)
                            .student(submission.getStudent())
                            .term(submission.getExam().getTerm())
                            .result(result)
                            .build());

            reportCard.setObtainedMarks(obtained);
            reportCard.setTotalMarks(subject.getMaxMarks());
            double percentage = (marks / subject.getMaxMarks().doubleValue()) * 100;
            reportCard.setPercentage(BigDecimal.valueOf(percentage).setScale(2, RoundingMode.HALF_UP));
            reportCard.setGrade(computeGrade(percentage));
            reportCard.setRemarks(generateRemarks(percentage));

            reportCardRepository.save(reportCard);
        }

        //Update submission summary
        submission.setObtainedMarks(totalObtained);
        submission.setStatus(SubmissionStatus.EVALUATED);
        submission.setEvaluatedBy(teacherId);
        submission.setEvaluatedAt(LocalDateTime.now());
        submissionRepository.save(submission);

        //Update overall Result
        BigDecimal totalExamMarks = submission.getExam().getTotalMarks();
        double overallPercentage = (totalObtained.doubleValue() / totalExamMarks.doubleValue()) * 100;

        result.setObtainedMarks(totalObtained);
        result.setPercentage(BigDecimal.valueOf(overallPercentage).setScale(2, RoundingMode.HALF_UP));
        result.setGrade(computeGrade(overallPercentage));
        result.setStatus(overallPercentage >= 35 ? ResultStatus.PASSED : ResultStatus.FAILED);
        result.setPublishedAt(LocalDateTime.now());

        Result savedResult = resultRepository.save(result);
        applicationEventPublisher.publishEvent(
                new ResultPublishedEvent(
                        savedResult.getResultId(),
                        List.of(studentId)  // send to this student’s parents
                )
        );
    }

    //Grade calculation
    private String computeGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 75) return "A";
        if (percentage >= 60) return "B";
        if (percentage >= 45) return "C";
        return "D";
    }

    //Remarks generation
    private String generateRemarks(double percentage) {
        if (percentage >= 90) return "Outstanding performance!";
        if (percentage >= 75) return "Excellent work!";
        if (percentage >= 60) return "Good effort, keep improving.";
        if (percentage >= 45) return "Fair effort, needs practice.";
        return "Poor performance, requires improvement.";
    }


    //    @Override
//    public List<?> getResultsByTeacher(Long teacherId) {
//        List<Exam> exams = examRepository.findByTeacher_Id(teacherId);
//        return exams.stream()
//                .map(e -> "Results for exam " + e.getExamName())
//                .toList();
//    }
    @Override
    public List<ResultResponse> getResultsByTeacher(Long teacherId) {
        List<Result> results = resultRepository.findByExam_Teacher_Id(teacherId);

        return results.stream()
                .map(r -> ResultResponse.builder()
                        .resultId(r.getResultId())
                        .examId(r.getExam().getExamId())
                        .studentId(r.getStudent().getStudentRegId())
                        .obtainedMarks(r.getObtainedMarks())
                        .percentage(r.getPercentage())
                        .grade(r.getGrade())
                        .status(r.getStatus().name())
                        .publishedAt(r.getPublishedAt())
                        .studentName(r.getStudent().getFirstName())
                        .examName(r.getExam().getExamName())
                        .subjectName(r.getExam().getSubject().getSubjectName())
                        .build())
                .collect(Collectors.toList());
    }


}

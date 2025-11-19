package com.digital.serviceimpl;
import com.digital.dto.*;
import com.digital.entity.*;
import com.digital.enums.ResultStatus;
import com.digital.enums.SubmissionStatus;
import com.digital.exception.ResourceNotFoundException;
import com.digital.repository.*;
import com.digital.servicei.StudentExamService;
import com.digital.servicei.TermService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.digital.enums.SubmissionStatus;


@Service
@RequiredArgsConstructor
public class StudentExamServiceImpl implements StudentExamService {

    private final StudentRepository studentRepository;
    private final ExamRepository examRepository;
    private final ExamQuestionRepository questionRepository;
    private final ExamSubmissionRepository submissionRepository;
    private final ResultRepository resultRepository;
    private final ReportCardRepository reportCardRepository;
    private final ClassRepository classRepository;
    private final SectionRepository sectionRepository;
    private final TeacherRepository teacherRepository;
    private final AttendanceRepository attendanceRepository;
    private final TermService termService;


    @Override
    public List<UpcomingExamResponse> getExamsForStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Long classId = student.getSchoolClass().getClassId();
        Long sectionId = student.getSection().getSectionId();

        LocalDateTime now = LocalDateTime.now();

        List<Exam> upcomingExams = examRepository.findAll().stream()
                .filter(e -> e.getSchoolClass().getClassId().equals(classId))
                .filter(e -> e.getSection().getSectionId().equals(sectionId))
                .filter(e -> e.getEndTime().isAfter(now)) // <-- include exams that are ongoing
                .toList();

        return upcomingExams.stream()
                .map(this::convertToResponse)
                .toList();
    }


    @Override
    public void submitExam (Long examId, Long studentId, SubmissionRequest request){
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        try {
            // Convert answers list to JSON string if needed
            String answersJson = new ObjectMapper().writeValueAsString(request.getAnswers());

            ExamSubmission submission = ExamSubmission.builder()
                    .exam(exam)
                    .student(student)
                    .answers(answersJson)
                    .submittedAt(LocalDateTime.now())
                    .status(SubmissionStatus.PENDING)
                    .build();

            submissionRepository.save(submission);
        } catch (Exception e) {
            throw new RuntimeException("Error while submitting exam: " + e.getMessage(), e);
        }
    }

    public List<SubmissionResponse> getResults(Long studentId) {
        return resultRepository.findByStudent_StudentRegId(studentId)
                .stream()
                .map(result -> {
                    // Fetch subject-wise report cards linked to this result
                    List<SubjectResultResponse> subjectResults = reportCardRepository
                            .findByResult_ResultId(result.getResultId())
                            .stream()
                            .map(rc -> SubjectResultResponse.builder()
                                    .subjectId(rc.getSubject().getSubjectId())
                                    .subjectName(rc.getSubject().getSubjectName())
                                    .obtainedMarks(rc.getObtainedMarks())
                                    .totalMarks(rc.getTotalMarks())
                                    .percentage(rc.getPercentage())
                                    .grade(rc.getGrade())
                                    .remarks(rc.getRemarks())
                                    .build())
                            .collect(Collectors.toList());

                    return SubmissionResponse.builder()
                            .submissionId(result.getResultId())
                            .examId(result.getExam().getExamId())
                            .studentId(result.getStudent().getStudentRegId())
                            .obtainedMarks(result.getObtainedMarks())
                            .percentage(result.getPercentage())
                            .grade(result.getGrade())
                            .resultStatus(result.getStatus() != null ? result.getStatus() : ResultStatus.PENDING)
                            .subjects(subjectResults)
                            .build();

                })
                .collect(Collectors.toList());
    }


    @Override
    public ReportCardResponse getReportCard(Long studentId, String term) {
        ReportCard reportCard = reportCardRepository
                .findByStudentStudentRegIdAndTerm(studentId, term.trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Report card not found for this student and term"));

        return ReportCardResponse.builder()
                .reportCardId(reportCard.getReportCardId())
                .studentId(reportCard.getStudent().getStudentRegId())
                .term(reportCard.getTerm())
                .totalMarks(reportCard.getTotalMarks())
                .obtainedMarks(reportCard.getObtainedMarks())
                .percentage(reportCard.getPercentage())
                .grade(reportCard.getGrade())
                .remarks(reportCard.getRemarks())
                .generatedAt(reportCard.getGeneratedAt())
                .build();
    }

    private UpcomingExamResponse convertToResponse(Exam exam) {
        Long classId = exam.getSchoolClass() != null ? exam.getSchoolClass().getClassId() : null;
        Long sectionId = exam.getSection() != null ? exam.getSection().getSectionId() : null;
        Long subjectId = exam.getSubject() != null ? exam.getSubject().getSubjectId() : null;
        Long teacherId = exam.getTeacher() != null ? exam.getTeacher().getId() : null;

        String className = exam.getSchoolClass() != null ? exam.getSchoolClass().getClassName() : "Unknown Class";
        String sectionName = exam.getSection() != null ? exam.getSection().getSectionName() : "Unknown Section";
        String subjectName = exam.getSubject() != null ? exam.getSubject().getSubjectName() : "Unknown Subject";
        String teacherName = exam.getTeacher() != null
                ? exam.getTeacher().getFirstName() + " " + exam.getTeacher().getLastName()
                : "Unknown Teacher";

        return UpcomingExamResponse.builder()
                .examId(exam.getExamId())
                .classId(classId)
                .className(className)
                .sectionId(sectionId)
                .sectionName(sectionName)
                .subjectId(subjectId)
                .subjectName(subjectName)
                .teacherId(teacherId)
                .teacherName(teacherName)
                .examType(exam.getExamType().name())
                .examName(exam.getExamName())
                .startTime(exam.getStartTime())
                .endTime(exam.getEndTime())
                .duration(exam.getDuration())
                .totalMarks(exam.getTotalMarks())
                .build();
    }

    private String computeGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 75) return "A";
        if (percentage >= 60) return "B";
        if (percentage >= 45) return "C";
        return "D";
    }
    @Override
    @Transactional(readOnly = true)
    public ReportCardViewDto getReportCardView(Long studentRegId, String term) {
        List<ReportCard> reportCards = reportCardRepository.findAllByStudentStudentRegIdAndTerm(studentRegId, term);
        if (reportCards.isEmpty()) throw new ResourceNotFoundException("No report cards");

        Student student = reportCards.get(0).getStudent();

        // Dynamic subject list
        List<String> subjectNames = reportCards.stream()
                .map(rc -> rc.getSubject().getSubjectName())
                .collect(Collectors.toList());
        String subjectsStr = String.join(", ", subjectNames);

        // get term date range dynamically
        Long examId = reportCards.get(0).getSubmission().getExam().getExamId();
        Map<String, LocalDate> range = termService.getTermDateRange(examId, term);
        LocalDate termStart = range.get("startDate");
        LocalDate termEnd = range.get("endDate");

        // calculate attendance
        Long presentDays = attendanceRepository.countDaysByStatus(studentRegId,
                com.digital.enums.AttendanceStatus.PRESENT, termStart, termEnd);

        Long totalDays = attendanceRepository.countTotalDays(studentRegId, termStart, termEnd);

        String attendancePercent = totalDays != 0
                ? BigDecimal.valueOf(presentDays * 100.0 / totalDays)
                .setScale(2, RoundingMode.HALF_UP).toString() + "%"
                : "0%";

        Long leavesTaken = totalDays - presentDays;


        // Dynamic teacher remark
        String teacherRemark = student.getFirstName() + " " + student.getLastName() +
                " has shown excellent improvement in " + subjectsStr + ". Overall good performance.";

        // Build subject DTOs
        List<ReportCardSubjectDto> subjects = reportCards.stream()
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

        BigDecimal totalMarks = reportCards.stream()
                .map(ReportCard::getTotalMarks).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalObtained = reportCards.stream()
                .map(ReportCard::getObtainedMarks).reduce(BigDecimal.ZERO, BigDecimal::add);
        String overallPercentage = totalObtained.multiply(BigDecimal.valueOf(100))
                .divide(totalMarks, 2, RoundingMode.HALF_UP).toString();

        //Long examId = reportCards.get(0).getSubmission().getExam().getExamId();
        Result result = resultRepository.findByStudent_StudentRegIdAndExam_ExamId(studentRegId, examId).orElse(null);

        return ReportCardViewDto.builder()
                .studentRegId(studentRegId)
                .schoolName("ABC International School")
                .schoolAddress("123 School Road, Pune, India")
                .term(term)
                .studentName(student.getFirstName() + " " + student.getLastName())
                .className(student.getSchoolClass().getClassName())
                .rollNo(student.getRollNumber())
                .admissionNo(student.getAdmissionNumber())
                .academicYear(student.getAcademicYear())
                .subjects(subjects)
                .totalMarks(totalMarks)
                .totalObtained(totalObtained)
                .overallPercentage(overallPercentage)
                .overallGrade(result != null ? result.getGrade() : "-")
                .resultStatus(result != null ? (result.getStatus() != null ? result.getStatus().name() : "-") : "-")
                .attendancePercent("92%")
                .leavesTaken(3)
                .teacherRemarks(teacherRemark)
                .generatedDate(LocalDate.now())
                .build();
    }

}

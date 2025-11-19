package com.digital.serviceimpl;
import com.digital.entity.*;
import com.digital.events.ReportCardGeneratedEvent;
import com.digital.repository.*;
import com.digital.servicei.ReportCardService;
import com.digital.servicei.StudentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ReportCardServiceImpl implements ReportCardService {

    private final ReportCardRepository reportCardRepository;
    private final ResultRepository resultRepository;
    private final StudentRepository studentRepository;
    private final ExamRepository examRepository;
    private final StudentService studentService;
    private final SubjectRepository subjectRepository;
    private final ExamSubmissionRepository examSubmissionRepository;
    private final ApplicationEventPublisher publisher;
    private final SessionRepository sessionRepository;



    @Override
    public List<ReportCard> generateReportCards(Long classId, Long sectionId, Long subjectId, String term, List<Long> examIds) {

        // Fetch all students in the given class+section
        List<Student> students = studentRepository.findBySchoolClass_ClassIdAndSection_SectionId(classId, sectionId);
        if (students.isEmpty()) {
            throw new EntityNotFoundException("No students found for the given class and section");
        }

        //Fetch subject
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found"));

        List<ReportCard> reportCards = new ArrayList<>();
        List<Long> studentIds = new ArrayList<>();

        // Loop through each student
        for (Student student : students) {

            for (Long examId : examIds) {

                // Fetch Result for this student + exam
                Result result = resultRepository.findByStudent_StudentRegIdAndExam_ExamId(student.getUser().getUserId(), examId)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Result not found for student " + student.getUser().getUserId() + " and exam " + examId));

                // Generate random marks for example (replace with actual logic if available)
                BigDecimal totalMarks = new BigDecimal("100");
                BigDecimal obtainedMarks = new BigDecimal(String.valueOf(50 + (int) (Math.random() * 50)));
                BigDecimal percentage = obtainedMarks.multiply(BigDecimal.valueOf(100))
                        .divide(totalMarks, 2, RoundingMode.HALF_UP);

                String grade = getGrade(percentage);
                String remarks = percentage.compareTo(new BigDecimal("50")) >= 0 ? "Passed" : "Needs Improvement";
                ExamSubmission submission = examSubmissionRepository
                        .findByStudent_User_UserIdAndExam_ExamId(student.getUser().getUserId(), examId)
                        .orElseThrow(() -> new RuntimeException("ExamSubmission not found"));

                // Build ReportCard
                ReportCard reportCard = ReportCard.builder()
                        .student(student)
                        .subject(subject)
                        .term(term)
                        .result(result)
                        .totalMarks(totalMarks)
                        .obtainedMarks(obtainedMarks)
                        .percentage(percentage)
                        .grade(grade)
                        .remarks(remarks)
                        .submission(submission)
                        .build();

                ReportCard saved = reportCardRepository.save(reportCard);
                reportCards.add(saved);
                studentIds.add(student.getUser().getUserId());

                // Publish event for this student's report card
                publisher.publishEvent(new ReportCardGeneratedEvent(saved.getReportCardId(), List.of(student.getUser().getUserId())));
            }
        }

        // Return all generated report cards
        return reportCards;
    }

    private String getGrade(BigDecimal percentage) {
        if (percentage.compareTo(new BigDecimal("90")) >= 0) return "A+";
        else if (percentage.compareTo(new BigDecimal("80")) >= 0) return "A";
        else if (percentage.compareTo(new BigDecimal("70")) >= 0) return "B+";
        else if (percentage.compareTo(new BigDecimal("60")) >= 0) return "B";
        else if (percentage.compareTo(new BigDecimal("50")) >= 0) return "C";
        else return "D";
    }

    @Override
    public LocalDate[] getTermDateRange(Long classId, String term) {
        List<LocalDate> sessionDates = sessionRepository.findAllBySchoolClass_ClassId(classId)
                .stream().map(Session::getDate)
                .sorted()
                .toList();

        if (sessionDates.isEmpty()) {
            return new LocalDate[]{LocalDate.now(), LocalDate.now()};
        }
        return new LocalDate[]{sessionDates.get(0), sessionDates.get(sessionDates.size() - 1)};
    }
}

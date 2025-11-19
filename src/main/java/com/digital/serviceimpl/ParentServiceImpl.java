package com.digital.serviceimpl;

import com.digital.dto.*;
import java.math.BigDecimal;

import com.digital.entity.*;
import com.digital.enums.AttendanceStatus;
import com.digital.enums.InvoiceStatus;
import com.digital.enums.Relationship;
import com.digital.enums.SubmissionStatus;
import com.digital.exception.ResourceNotFoundException;
import com.digital.exception.UnauthorizedAccessException;
import com.digital.repository.*;
import com.digital.servicei.ParentService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


//import static jdk.javadoc.internal.doclets.toolkit.util.DocPath.parent;


@Service
//@RequiredArgsConstructor
@Transactional
public class ParentServiceImpl implements ParentService {
    private final ParentRepository parentRepository;
    private final ParentStudentMappingRepository mappingRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ParentStudentMappingRepository parentStudentMappingRepository;
    private final AttendanceRepository attendanceRepository;
    private final ExamRepository examRepository;
    private final ResultRepository resultRepository;
    private final NotificationRepository notificationRepository;
    private final ExamNotificationRepository examNotificationRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final PTMRepository ptmRepository;


    @Autowired
    private AssignmentSubmissionRepository assignmentSubmissionRepository;
private ParentStudentMappingRepository studentMappingRepository;
private SubjectRepository subjectRepository;
private PaymentRepository paymentRepository;
private InvoiceRepository invoiceRepository;

    public ParentServiceImpl(ParentRepository parentRepository,PTMRepository ptmRepository, ParentStudentMappingRepository mappingRepository, UserRepository userRepository, StudentRepository studentRepository, ParentStudentMappingRepository parentStudentMappingRepository, AttendanceRepository attendanceRepository, ExamRepository examRepository, ResultRepository resultRepository, NotificationRepository notificationRepository, ExamNotificationRepository examNotificationRepository, AssignmentRepository assignmentRepository, SubmissionRepository submissionRepository, SubjectRepository subjectRepository, PaymentRepository paymentRepository, InvoiceRepository invoiceRepository) {
        this.parentRepository = parentRepository;
        this.mappingRepository = mappingRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.parentStudentMappingRepository = parentStudentMappingRepository;
        this.attendanceRepository = attendanceRepository;
        this.examRepository = examRepository;
        this.resultRepository = resultRepository;
        this.notificationRepository = notificationRepository;
        this.examNotificationRepository = examNotificationRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.subjectRepository = subjectRepository;
     this.paymentRepository = paymentRepository;
this.invoiceRepository = invoiceRepository;
this.ptmRepository = ptmRepository;
    }


    @Override
    public ParentResponse createParent(ParentRequest req) {
        // Check if parent already exists for this user
        if (parentRepository.existsByUserUserId(req.getUserId())) {
            throw new IllegalArgumentException("Parent already exists for userId=" + req.getUserId());
        }

        //Check if User exists
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException(
                        "Failed to create parent: User not found with ID: " + req.getUserId()
                ));

        Parent parent = Parent.builder()
                .user(user) // link to existing User
                .name(req.getName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .address(req.getAddress())
                .relationship(Relationship.valueOf(req.getRelationship()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Parent saved = parentRepository.save(parent);
        return toResponse(saved);
    }


    @Override
    public ParentResponse updateParent(Long parentId, ParentRequest req) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found " + parentId));
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found " + req.getUserId()));
        parent.setUser(user);
        parent.setName(req.getName());
        parent.setEmail(req.getEmail());
        parent.setPhone(req.getPhone());
        parent.setAddress(req.getAddress());
        parent.setRelationship(Relationship.valueOf(req.getRelationship()));
        Parent updated = parentRepository.save(parent);
        return toResponse(updated);
    }

    @Override
    public void deleteParent(Long parentId) {
        Parent p = parentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found " + parentId));
        // optionally remove mappings first
        mappingRepository.findByParentId(parentId)
                .forEach(mappingRepository::delete);
        parentRepository.delete(p);
    }

    @Override
    public List<ParentResponse> getAllParents() {
        return parentRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public ParentResponse getParentById(Long parentId) {
        Parent p = parentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found " + parentId));
        return toResponse(p);
    }

    @Override
    public void linkParentToStudent(Long parentId, Long studentId, String relationshipType) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found " + parentId));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found " + studentId));

        if (mappingRepository.findByStudent(student).stream()
                .anyMatch(m -> m.getParent().getParentId().equals(parentId))) {
            return; // already linked
        }

        ParentStudentMapping mapping = ParentStudentMapping.builder()
                .parent(parent)
                .student(student)
                .relationshipType(Relationship.valueOf(relationshipType))
                .build();

        mappingRepository.save(mapping);
    }


    @Override
    public ParentDashboardResponse getDashboard(Long parentId) {
        // Fetch parent
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with ID: " + parentId));

        // Fetch all student mappings
        List<ParentStudentMapping> mappings = parentStudentMappingRepository.findByParent_ParentId(parentId);
        if (mappings.isEmpty()) throw new UnauthorizedAccessException("Parent not linked to any student");

        // Build child summaries
        List<ParentDashboardResponse.ChildSummary> children = mappings.stream().map(mapping -> {
            Student student = mapping.getStudent();
            Long studentRegId = student.getStudentRegId();

            // Attendance %
            List<Attendance> attendances = attendanceRepository.findByStudent_StudentRegId(studentRegId);
            double attendancePercent = attendances.isEmpty() ? 0.0 :
                    attendances.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count() * 100.0 / attendances.size();

            // Upcoming exams
            List<ParentDashboardResponse.UpcomingExamDto> exams = getExams(parentId, studentRegId);

            // Assignments
            Long classId = student.getSchoolClass() != null ? student.getSchoolClass().getClassId() : null;
            Long sectionId = student.getSection() != null ? student.getSection().getSectionId() : null;
            List<Assignment> allAssignments = (classId != null && sectionId != null) ?
                    assignmentRepository.findByClassIdAndSectionId(classId, sectionId) : Collections.emptyList();

            List<ParentDashboardResponse.AssignmentDto> assignmentDtos = allAssignments.stream().map(a -> {
                boolean submitted = assignmentSubmissionRepository
                        .findByAssignment_AssignmentIdAndStudentId(a.getAssignmentId(), studentRegId)
                        .stream().anyMatch(sub -> sub.getStatus() == SubmissionStatus.SUBMITTED);

                Subject subject = a.getSubjectId() != null ? subjectRepository.findById(a.getSubjectId()).orElse(null) : null;

                return ParentDashboardResponse.AssignmentDto.builder()
                        .assignmentId(a.getAssignmentId())
                        .title(a.getTitle())
                        .dueDate(a.getDueDate() != null ? a.getDueDate().toString() : null)
                        .submitted(submitted)
                        .subject_id(a.getSubjectId())
                        .subjectName(subject != null ? subject.getSubjectName() : null)
                        .build();
            }).collect(Collectors.toList());

            // Fee summary
            ParentDashboardResponse.FeeSummary feeSummary = getFeeSummaryForStudent(student);

            // PTMs for this student
            List<ParentDashboardResponse.PTMDto> ptmDtos = ptmRepository.findAll().stream()
                    .filter(ptm -> ptm.getStudents() != null &&
                            ptm.getStudents().stream()
                                    .map(Student::getStudentRegId)
                                    .anyMatch(id -> id.equals(studentRegId))
                    )
                    .map(ptm -> ParentDashboardResponse.PTMDto.builder()
                            .ptmId(ptm.getPtmId())
                            .title(ptm.getTitle())
                            .meetingDateTime(ptm.getMeetingDateTime() != null ? ptm.getMeetingDateTime().toString() : null)
                            //.type(ptm.getType() != null ? ptm.getType().name() : null)
                            .status(ptm.getStatus() != null ? ptm.getStatus().name() : null)
                            .joinLink(ptm.getJoinLink())
                            .venue(ptm.getVenue())
                            .build()
                    ).collect(Collectors.toList());

            return ParentDashboardResponse.ChildSummary.builder()
                    .studentRegId(studentRegId)
                    .studentName(student.getFirstName() + " " + student.getLastName())
                    .attendancePercent(attendancePercent)
                    .upcomingExams(exams)
                    .assignments(assignmentDtos)
                    .feeSummary(feeSummary)
                    .ptms(ptmDtos)

                    .build();
        }).collect(Collectors.toList());

        // Notifications
        List<ParentDashboardResponse.NotificationDto> generalNotifications = notificationRepository
                .findByParent_ParentIdOrderByCreatedAtDesc(parentId)
                .stream()
                .map(n -> ParentDashboardResponse.NotificationDto.builder()
                        .id(n.getId())
                        .title(n.getType())
                        .message(n.getMessage())
                        .type(n.getType())
                        .createdAt(n.getCreatedAt() != null ? n.getCreatedAt().toString() : null)
                        .build())
                .collect(Collectors.toList());

        List<ParentDashboardResponse.NotificationDto> examNotifications = examNotificationRepository
                .findByUserIdOrderByCreatedAtDesc(parent.getUser().getUserId())
                .stream()
                .map(n -> ParentDashboardResponse.NotificationDto.builder()
                        .id(n.getExam_notificationId())
                        .title(n.getTitle())
                        .message(n.getMessage())
                        .type(n.getType() != null ? n.getType().name() : "EXAM")
                        .createdAt(n.getCreatedAt() != null ? n.getCreatedAt().toString() : null)
                        .build())
                .collect(Collectors.toList());

        generalNotifications.addAll(examNotifications);

        return ParentDashboardResponse.builder()
                .parentId(parent.getParentId())
                .parentName(parent.getName())
                .userId(parent.getUser() != null ? parent.getUser().getUserId() : null)
                .children(children)
                .notifications(generalNotifications)
                .build();
    }
    private ParentDashboardResponse.FeeSummary getFeeSummaryForStudent(Student student) {
        List<Invoice> invoices = invoiceRepository.findByStudent_StudentRegId(student.getStudentRegId());

        if (invoices.isEmpty()) {
            return ParentDashboardResponse.FeeSummary.builder()
                    .totalFee(0.0)
                    .paidAmount(0.0)
                    .dueAmount(0.0)
                    .dueDate(null)
                    .build();
        }

        double totalFee = invoices.stream()
                .mapToDouble(inv -> inv.getFeeStructure() != null ? inv.getFeeStructure().getTotalAmount().doubleValue() : 0.0)
                .sum();

        double paidAmount = invoices.stream()
                .mapToDouble(inv -> inv.getAmountPaid() != null ? inv.getAmountPaid().doubleValue() : 0.0)
                .sum();

        double dueAmount = totalFee - paidAmount;

        // Earliest due date among unpaid invoices
        LocalDate dueDate = invoices.stream()
                .filter(inv -> inv.getStatus() != InvoiceStatus.PAID)
                .map(Invoice::getDueDate)
                .sorted()
                .findFirst()
                .orElse(null);

        return ParentDashboardResponse.FeeSummary.builder()
                .totalFee(totalFee)
                .paidAmount(paidAmount)
                .dueAmount(dueAmount)
                .dueDate(dueDate != null ? dueDate.toString() : null)
                .build();
    }

    @Override
    public List<Attendance> getAttendance(Long parentId, Long studentRegId) {
        validateParentAccess(parentId, studentRegId);
        return attendanceRepository.findByStudent_StudentRegId(studentRegId);
    }

    private void validateParentAccess(Long parentId, Long studentRegId) {
        if (!mappingRepository.existsByParent_ParentIdAndStudent_StudentRegId(parentId, studentRegId)) {
            throw new UnauthorizedAccessException("Parent not linked to this student");
        }
    }

    @Override
    public List<ParentDashboardResponse.UpcomingExamDto> getExams(Long parentId, Long studentRegId) {
        // Check parent has access to this student
        validateParentAccess(parentId, studentRegId);

        // Fetch student entity
        Student student = studentRepository.findById(studentRegId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // Current time
        LocalDateTime now = LocalDateTime.now();

        // Fetch upcoming exams based on student's class & section
        List<Exam> exams = examRepository.findBySchoolClassAndSectionAndStartTimeAfter(
                student.getSchoolClass(),
                student.getSection(),
                LocalDateTime.now()
        );


        // Map to DTO
        return exams.stream()
                .map(e -> ParentDashboardResponse.UpcomingExamDto.builder()
                        .examId(e.getExamId())
                        .title(e.getExamName())
                        .subject(e.getSubject() != null ? e.getSubject().getSubjectName() : "N/A")
                        .examDate(e.getStartTime().toLocalDate().toString())
                        .build())
                .toList();
    }

    @Override
    public List<ResultResponse> getResults(Long parentId, Long studentId) {
        // ensure parent is linked to this student
        validateParentAccess(parentId, studentId);

        List<Result> results = resultRepository.findByStudent_StudentRegId(studentId);

        return results.stream().map(result -> ResultResponse.builder()
                .resultId(result.getResultId())
                .examId(result.getExam().getExamId())
                .studentId(result.getStudent().getStudentRegId())
                .obtainedMarks(result.getObtainedMarks())
                .percentage(result.getPercentage())
                .grade(result.getGrade())
                .status(result.getStatus().name())
                .publishedAt(result.getPublishedAt())
                .studentName(result.getStudent().getFirstName() + " " + result.getStudent().getLastName())
                .examName(result.getExam().getExamName())
                .subjectName(result.getExam().getSubject().getSubjectName())
                .build()
        ).toList();
    }


    @Override
    public List<NotificationDto> getNotifications(Long parentId) {
        // Fetch the Parent entity
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with ID: " + parentId));

        // General notifications
        List<NotificationDto> generalNotifications = notificationRepository
                .findByParent_ParentIdOrderByCreatedAtDesc(parentId)
                .stream()
                .map(n -> NotificationDto.builder()
                        .id(n.getId())
                        .message(n.getMessage())
                        .type(n.getType())
                        .createdAt(n.getCreatedAt())
                        .status(n.getStatus())
                        .build())
                .collect(Collectors.toList());

        // Exam + Result notifications (both live in ExamNotification table)
        List<NotificationDto> examAndResultNotifications = examNotificationRepository
                .findByUserIdOrderByCreatedAtDesc(parent.getUser().getUserId())
                .stream()
                .map(n -> NotificationDto.builder()
                        .id(n.getExam_notificationId())
                        .message(n.getMessage())
                        // Convert enum safely to string (EXAM / RESULT / REPORT_CARD etc.)
                        .type(n.getType() != null ? n.getType().name() : "EXAM")
                        .createdAt(n.getCreatedAt() != null ? n.getCreatedAt() : LocalDateTime.now())
                        .status(n.isSeen() ? "READ" : "UNREAD")
                        .build())
                .collect(Collectors.toList());

        //Merge both lists (general + exam/result)
        generalNotifications.addAll(examAndResultNotifications);

        //  Sort merged list by createdAt (latest first)
        return generalNotifications.stream()
                .sorted(Comparator.comparing(NotificationDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }



    private ParentResponse toResponse(Parent parent) {
        return ParentResponse.builder()
                .parentId(parent.getParentId())
                .userId(parent.getUser() != null ? parent.getUser().getUserId() : null)
                .name(parent.getName())
                .email(parent.getEmail())
                .phone(parent.getPhone())
                .address(parent.getAddress())
                .relationship(parent.getRelationship().name())
                .createdAt(parent.getCreatedAt())
                .updatedAt(parent.getUpdatedAt())
                .students(null)
                .build();
    }

@Override
public List<AssignmentResponse> getAssignmentsForChild(Long parentId) {
    List<Long> studentRegIds = parentStudentMappingRepository.findStudentRegIdByParentId(parentId);
    List<AssignmentResponse> responses = new ArrayList<>();

    for (Long studentRegId : studentRegIds) {
        //Get student’s class and section
        Student student = studentRepository.findById(studentRegId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        //Fetch assignments for that class & section
        List<Assignment> assignments = assignmentRepository.findByClassIdAndSectionId(
                student.getSchoolClass().getClassId(),
                student.getSection().getSectionId()
        );

        for (Assignment assignment : assignments) {
            AssignmentResponse resp = new AssignmentResponse();
            resp.setAssignmentId(assignment.getAssignmentId());
            resp.setTitle(assignment.getTitle());
            resp.setDescription(assignment.getDescription());

            try {
                if (assignment.getFileUrl() != null) {
                    Blob blob = assignment.getFileUrl();
                    byte[] bytes = blob.getBytes(1, (int) blob.length());
                    String base64File = Base64.getEncoder().encodeToString(bytes);
                    resp.setFileName(base64File);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            resp.setDueDate(assignment.getDueDate());
            resp.setCreatedAt(assignment.getCreatedAt());
            resp.setUpdatedAt(assignment.getUpdatedAt());
            resp.setClassId(assignment.getClassId());
            resp.setSectionId(assignment.getSectionId());
            resp.setSubjectId(assignment.getSubjectId());
            resp.setTeacherId(assignment.getTeacherId());

            // Check if student has submitted
            Optional<AssignmentSubmission> submissionOpt =
                    assignmentSubmissionRepository.findByAssignment_AssignmentIdAndStudentId(
                            assignment.getAssignmentId(),
                            studentRegId
                    );

            if (submissionOpt.isPresent()) {
                AssignmentSubmission submission = submissionOpt.get();
                resp.setSubmitted(true);
                resp.setSubmittedAt(submission.getSubmittedAt());
                resp.setStatus(submission.getStatus());
                resp.setMarks(submission.getMarks());
                resp.setFeedback(submission.getFeedback());
            } else {
                resp.setSubmitted(false);
            }

            responses.add(resp);
        }
    }

    return responses;
}

}
//package com.digital.serviceimpl;
//
//import com.digital.dto.*;
//import com.digital.entity.*;
//import com.digital.enums.*;
//import com.digital.exception.ResourceNotFoundException;
//import com.digital.exception.UnauthorizedAccessException;
//import com.digital.repository.*;
//import com.digital.servicei.ParentService;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class ParentServiceImpl implements ParentService {
//
//    private final ParentRepository parentRepository;
//    private final ParentStudentMappingRepository parentStudentMappingRepository;
//    private final UserRepository userRepository;
//    private final StudentRepository studentRepository;
//    private final AttendanceRepository attendanceRepository;
//    private final ExamRepository examRepository;
//    private final ResultRepository resultRepository;
//    private final NotificationRepository notificationRepository;
//    private final ExamNotificationRepository examNotificationRepository;
//    private final AssignmentRepository assignmentRepository;
//    private final AssignmentSubmissionRepository assignmentSubmissionRepository;
//    private final SubjectRepository subjectRepository;
//    private final PTMRepository ptmRepository;
//    private final PaymentRepository paymentRepository;
//    private final InvoiceRepository invoiceRepository;

//    @Override
//    public ParentDashboardResponse getDashboard(Long parentId) {
//        // Fetch parent
//        Parent parent = parentRepository.findById(parentId)
//                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with ID: " + parentId));
//
//        // Fetch all student mappings
//        List<ParentStudentMapping> mappings = parentStudentMappingRepository.findByParent_ParentId(parentId);
//        if (mappings.isEmpty()) throw new UnauthorizedAccessException("Parent not linked to any student");
//
//        // Build child summaries
//        List<ParentDashboardResponse.ChildSummary> children = mappings.stream().map(mapping -> {
//            Student student = mapping.getStudent();
//            Long studentRegId = student.getStudentRegId();
//
//            // Attendance %
//            List<Attendance> attendances = attendanceRepository.findByStudent_StudentRegId(studentRegId);
//            double attendancePercent = attendances.isEmpty() ? 0.0 :
//                    attendances.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count() * 100.0 / attendances.size();
//
//            // Upcoming exams
//            List<ParentDashboardResponse.UpcomingExamDto> exams = getExams(parentId, studentRegId);
//
//            // Assignments
//            Long classId = student.getSchoolClass() != null ? student.getSchoolClass().getClassId() : null;
//            Long sectionId = student.getSection() != null ? student.getSection().getSectionId() : null;
//            List<Assignment> allAssignments = (classId != null && sectionId != null) ?
//                    assignmentRepository.findByClassIdAndSectionId(classId, sectionId) : Collections.emptyList();
//
//            List<ParentDashboardResponse.AssignmentDto> assignmentDtos = allAssignments.stream().map(a -> {
//                boolean submitted = assignmentSubmissionRepository
//                        .findByAssignment_AssignmentIdAndStudentId(a.getAssignmentId(), studentRegId)
//                        .stream().anyMatch(sub -> sub.getStatus() == SubmissionStatus.SUBMITTED);
//
//                Subject subject = a.getSubjectId() != null ? subjectRepository.findById(a.getSubjectId()).orElse(null) : null;
//
//                return ParentDashboardResponse.AssignmentDto.builder()
//                        .assignmentId(a.getAssignmentId())
//                        .title(a.getTitle())
//                        .dueDate(a.getDueDate() != null ? a.getDueDate().toString() : null)
//                        .submitted(submitted)
//                        .subject_id(a.getSubjectId())
//                        .subjectName(subject != null ? subject.getSubjectName() : null)
//                        .build();
//            }).collect(Collectors.toList());
//
//            // Fee summary
//            ParentDashboardResponse.FeeSummary feeSummary = getFeeSummaryForStudent(student);
//
//            // PTMs for this student
//            List<ParentDashboardResponse.PTMDto> ptmDtos = ptmRepository.findAll().stream()
//                    .filter(ptm -> ptm.getStudents() != null &&
//                            ptm.getStudents().stream()
//                                    .map(Student::getStudentRegId)
//                                    .anyMatch(id -> id.equals(studentRegId))
//                    )
//                    .map(ptm -> ParentDashboardResponse.PTMDto.builder()
//                            .ptmId(ptm.getPtmId())
//                            .title(ptm.getTitle())
//                            .meetingDateTime(ptm.getMeetingDateTime() != null ? ptm.getMeetingDateTime().toString() : null)
//                            //.type(ptm.getType() != null ? ptm.getType().name() : null)
//                            .status(ptm.getStatus() != null ? ptm.getStatus().name() : null)
//                            .joinLink(ptm.getJoinLink())
//                            .venue(ptm.getVenue())
//                            .build()
//                    ).collect(Collectors.toList());
//
//            return ParentDashboardResponse.ChildSummary.builder()
//                    .studentRegId(studentRegId)
//                    .studentName(student.getFirstName() + " " + student.getLastName())
//                    .attendancePercent(attendancePercent)
//                    .upcomingExams(exams)
//                    .assignments(assignmentDtos)
//                    .feeSummary(feeSummary)
//                   // .ptms(ptmDtos)
//
//                    .build();
//        }).collect(Collectors.toList());
//
//        // Notifications
//        List<ParentDashboardResponse.NotificationDto> generalNotifications = notificationRepository
//                .findByParent_ParentIdOrderByCreatedAtDesc(parentId)
//                .stream()
//                .map(n -> ParentDashboardResponse.NotificationDto.builder()
//                        .id(n.getId())
//                        .title(n.getType())
//                        .message(n.getMessage())
//                        .type(n.getType())
//                        .createdAt(n.getCreatedAt() != null ? n.getCreatedAt().toString() : null)
//                        .build())
//                .collect(Collectors.toList());
//
//        List<ParentDashboardResponse.NotificationDto> examNotifications = examNotificationRepository
//                .findByUserIdOrderByCreatedAtDesc(parent.getUser().getUserId())
//                .stream()
//                .map(n -> ParentDashboardResponse.NotificationDto.builder()
//                        .id(n.getExam_notificationId())
//                        .title(n.getTitle())
//                        .message(n.getMessage())
//                        .type(n.getType() != null ? n.getType().name() : "EXAM")
//                        .createdAt(n.getCreatedAt() != null ? n.getCreatedAt().toString() : null)
//                        .build())
//                .collect(Collectors.toList());
//
//        generalNotifications.addAll(examNotifications);
//
//        return ParentDashboardResponse.builder()
//                .parentId(parent.getParentId())
//                .parentName(parent.getName())
//                .userId(parent.getUser() != null ? parent.getUser().getUserId() : null)
//                .children(children)
//                .notifications(generalNotifications)
//                .build();
//    }

//    private ParentDashboardResponse.FeeSummary getFeeSummaryForStudent(Student student) {
//        List<Invoice> invoices = invoiceRepository.findByStudent_StudentRegId(student.getStudentRegId());
//
//        if (invoices.isEmpty()) {
//            return ParentDashboardResponse.FeeSummary.builder()
//                    .totalFee(0.0)
//                    .paidAmount(0.0)
//                    .dueAmount(0.0)
//                    .dueDate(null)
//                    .build();
//        }
//
//        double totalFee = invoices.stream()
//                .mapToDouble(inv -> inv.getFeeStructure() != null ? inv.getFeeStructure().getTotalAmount().doubleValue() : 0.0)
//                .sum();
//
//        double paidAmount = invoices.stream()
//                .mapToDouble(inv -> inv.getAmountPaid() != null ? inv.getAmountPaid().doubleValue() : 0.0)
//                .sum();
//
//        double dueAmount = totalFee - paidAmount;
//
//        LocalDate dueDate = invoices.stream()
//                .filter(inv -> inv.getStatus() != InvoiceStatus.PAID)
//                .map(Invoice::getDueDate)
//                .sorted()
//                .findFirst().orElse(null);
//
//        return ParentDashboardResponse.FeeSummary.builder()
//                .totalFee(totalFee)
//                .paidAmount(paidAmount)
//                .dueAmount(dueAmount)
//                .dueDate(dueDate != null ? dueDate.toString() : null)
//                .build();
//    }
//
//    @Override
//    public List<ParentDashboardResponse.UpcomingExamDto> getExams(Long parentId, Long studentRegId) {
//        if (!parentStudentMappingRepository.existsByParent_ParentIdAndStudent_StudentRegId(parentId, studentRegId)) {
//            throw new UnauthorizedAccessException("Parent not linked to this student");
//        }
//
//        Student student = studentRepository.findById(studentRegId)
//                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
//
//        List<Exam> exams = examRepository.findBySchoolClassAndSectionAndStartTimeAfter(
//                student.getSchoolClass(),
//                student.getSection(),
//                LocalDateTime.now()
//        );
//
//        return exams.stream()
//                .map(e -> ParentDashboardResponse.UpcomingExamDto.builder()
//                        .examId(e.getExamId())
//                        .title(e.getExamName())
//                        .subject(e.getSubject() != null ? e.getSubject().getSubjectName() : "N/A")
//                        .examDate(e.getStartTime().toLocalDate().toString())
//                        .build())
//                .collect(Collectors.toList());
//    }
//
//}

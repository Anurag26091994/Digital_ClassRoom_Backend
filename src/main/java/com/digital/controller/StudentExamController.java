package com.digital.controller;


import com.digital.dto.*;
import com.digital.entity.ReportCard;
import com.digital.entity.Result;
import com.digital.entity.Student;
import com.digital.entity.Session;

import com.digital.exception.ResourceNotFoundException;
import com.digital.repository.AttendanceRepository;
import com.digital.repository.ReportCardRepository;
import com.digital.repository.ResultRepository;
import com.digital.repository.SessionRepository;
import com.digital.servicei.ReportCardService;
import com.digital.servicei.StudentExamService;
import com.digital.servicei.StudentService;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;


import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.io.File;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;             // ✅ Correct Document
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;



@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@CrossOrigin("*")
public class StudentExamController {
    private final ReportCardService reportCardService;
    private final StudentExamService studentExamService;
    private final StudentService studentService;
    private final ReportCardRepository reportCardRepository;
    private final ResultRepository resultRepository;
    private final AttendanceRepository attendanceRepository;
    private final SessionRepository sessionRepository;



    // GET /student/exams → Fetch upcoming exams
    @GetMapping("/exams")
    public ResponseEntity<List<UpcomingExamResponse>> getExams(@RequestParam Long studentId) {
        return ResponseEntity.ok(studentExamService.getExamsForStudent(studentId));
    }


    // POST /student/exams/{examId}/submit → Submit answers
    @PostMapping("/exams/{examId}/submit")
    public ResponseEntity<String> submitExam(@PathVariable Long examId,
                                             @RequestParam Long studentId,
                                             @RequestBody SubmissionRequest request) {
        studentExamService.submitExam(examId, studentId, request);
        return ResponseEntity.ok("Exam submitted successfully");
    }

    // GET /student/results → Fetch results
    @GetMapping("/results")
    public ResponseEntity<List<SubmissionResponse>> getResults(@RequestParam Long studentId) {
        return ResponseEntity.ok(studentExamService.getResults(studentId));
    }

    //@GetMapping("/{term}/view")
//@RolesAllowed({"STUDENT", "PARENT"})
//public String viewReportCard(@PathVariable String term,
//                             @RequestParam Long studentRegId,
//                             Model model) {
//
//    ReportCardViewDto rc = studentExamService.getReportCardView(studentRegId, term);
//    model.addAttribute("rc", rc);
//    return "report-card"; // maps to templates/report-card.html
//}
    @GetMapping("/{term}/view")
    @RolesAllowed({"STUDENT", "PARENT"})
    public ResponseEntity<ReportCardViewDto> viewReportCard(
            @PathVariable String term,
            @RequestParam Long studentRegId) {

        ReportCardViewDto rc = studentExamService.getReportCardView(studentRegId, term);
        return ResponseEntity.ok(rc);
    }

    @GetMapping("/{term}/download")
    @RolesAllowed({"STUDENT", "PARENT"})
    public ResponseEntity<String> downloadReportCardUrl(@PathVariable String term,
                                                        @RequestParam Long studentRegId) {
        List<ReportCard> reportCards = reportCardRepository
                .findAllByStudentStudentRegIdAndTerm(studentRegId, term);

        if (reportCards.isEmpty()) {
            return ResponseEntity.status(404).body("No report cards found for this student and term");
        }

        Student student = reportCards.get(0).getStudent();

        try {
            String directory = "reports"; // project-root/reports
            File folder = new File(directory);
            if (!folder.exists()) folder.mkdirs();

            String fileName = "ReportCard_" + studentRegId + "_" + term.replaceAll("\\s+", "_") + ".pdf";
            String filePath = Paths.get(directory, fileName).toString();

            try (PdfWriter writer = new PdfWriter(filePath);
                 PdfDocument pdfDoc = new PdfDocument(writer);
                 Document document = new Document(pdfDoc)) {

                PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

                // School header
                document.add(new Paragraph("ABC International School")
                        .setFont(boldFont).setFontSize(18).setTextAlignment(TextAlignment.CENTER));
                document.add(new Paragraph("Address: 123 School Road, Pune, India")
                        .setTextAlignment(TextAlignment.CENTER));
                document.add(new Paragraph("Term: " + term)
                        .setFont(boldFont).setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(10));

                // Student details
                Table studentTable = new Table(UnitValue.createPercentArray(new float[]{1,1})).useAllAvailableWidth();
                studentTable.addCell(new Cell().add(new Paragraph("Name: " + student.getFirstName() + " " + student.getLastName())));
                studentTable.addCell(new Cell().add(new Paragraph("Class: " + student.getSchoolClass().getClassName())));
                studentTable.addCell(new Cell().add(new Paragraph("Roll No: " + student.getRollNumber())));
                studentTable.addCell(new Cell().add(new Paragraph("Admission No: " + student.getAdmissionNumber())));
                studentTable.addCell(new Cell().add(new Paragraph("Academic Year: " + student.getAcademicYear())));
                document.add(studentTable);
                document.add(new Paragraph("\n"));

                // Exam results
                Table resultTable = new Table(UnitValue.createPercentArray(new float[]{3,2,2,2,3})).useAllAvailableWidth();
                resultTable.addHeaderCell("Subject");
                resultTable.addHeaderCell("Max Marks");
                resultTable.addHeaderCell("Obtained Marks");
                resultTable.addHeaderCell("Percentage");
                resultTable.addHeaderCell("Grade / Remarks");

                BigDecimal totalMarks = BigDecimal.ZERO;
                BigDecimal totalObtained = BigDecimal.ZERO;

                for (ReportCard rc : reportCards) {
                    resultTable.addCell(rc.getSubject().getSubjectName());
                    resultTable.addCell(rc.getTotalMarks().toString());
                    resultTable.addCell(rc.getObtainedMarks().toString());
                    resultTable.addCell(rc.getPercentage() + "%");
                    resultTable.addCell(rc.getGrade() + " - " + rc.getRemarks());

                    totalMarks = totalMarks.add(rc.getTotalMarks());
                    totalObtained = totalObtained.add(rc.getObtainedMarks());
                }

                document.add(resultTable);

                // Summary
                BigDecimal overallPercentage = totalObtained
                        .multiply(BigDecimal.valueOf(100))
                        .divide(totalMarks, 2, RoundingMode.HALF_UP);

                Long examId = reportCards.get(0).getSubmission().getExam().getExamId();
                Result result = resultRepository.findByStudent_StudentRegIdAndExam_ExamId(studentRegId, examId).orElse(null);

                document.add(new Paragraph("\nSummary").setFont(boldFont));
                Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{3,2})).useAllAvailableWidth();
                summaryTable.addCell("Total Marks"); summaryTable.addCell(totalMarks.toString());
                summaryTable.addCell("Obtained Marks"); summaryTable.addCell(totalObtained.toString());
                summaryTable.addCell("Percentage"); summaryTable.addCell(overallPercentage + "%");
                summaryTable.addCell("Grade"); summaryTable.addCell(result != null ? result.getGrade() : "-");
                summaryTable.addCell("Result Status"); summaryTable.addCell(result != null && result.getStatus() != null ? result.getStatus().name() : "-");
                document.add(summaryTable);

                // Attendance calculation using ReportCardService
                LocalDate[] termRange = reportCardService.getTermDateRange(
                        student.getSchoolClass().getClassId(), term);
                LocalDate termStart = termRange[0];
                LocalDate termEnd = termRange[1];

                Long presentDays = attendanceRepository.countDaysByStatus(
                        studentRegId,
                        com.digital.enums.AttendanceStatus.PRESENT,
                        termStart,
                        termEnd
                );
                Long totalDays = attendanceRepository.countTotalDays(studentRegId, termStart, termEnd);

                String attendancePercent = totalDays != 0
                        ? BigDecimal.valueOf(presentDays * 100.0 / totalDays)
                        .setScale(2, RoundingMode.HALF_UP)
                        .toString() + "%"
                        : "0%";

                Long leavesTaken = totalDays - presentDays;

                document.add(new Paragraph("\nAttendance").setFont(boldFont));
                Table attendanceTable = new Table(UnitValue.createPercentArray(new float[]{3,2})).useAllAvailableWidth();
                attendanceTable.addCell("Attendance %"); attendanceTable.addCell(attendancePercent);
                attendanceTable.addCell("Leaves Taken"); attendanceTable.addCell(String.valueOf(leavesTaken));
                document.add(attendanceTable);

                // Teacher remarks
                String studentName = student.getFirstName() + " " + student.getLastName();
                String subjectsStr = reportCards.stream()
                        .map(rc -> rc.getSubject().getSubjectName())
                        .collect(Collectors.joining(", "));

                String teacherRemark = studentName + " has shown excellent improvement in " + subjectsStr + ". Overall good performance.";

                document.add(new Paragraph("\nTeacher Remarks").setFont(boldFont));
                document.add(new Paragraph(teacherRemark));
            }

            String fileUrl = "http://localhost:8080/reports/" + fileName;
            return ResponseEntity.ok(fileUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error generating PDF: " + e.getMessage());
        }
    }


}

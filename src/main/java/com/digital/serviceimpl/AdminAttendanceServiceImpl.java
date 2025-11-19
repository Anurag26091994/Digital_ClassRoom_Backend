package com.digital.serviceimpl;

import com.digital.dto.GeneratePdfReportRequest;
import com.digital.entity.Attendance;
import com.digital.entity.SchoolClass;
import com.digital.entity.Section;
import com.digital.entity.Student;
import com.digital.enums.AttendanceStatus;
import com.digital.enums.MarkBy;
import com.digital.exception.ResourceNotFoundException;
import com.digital.repository.*;
import com.digital.servicei.AdminAttendanceService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

@Service
public class AdminAttendanceServiceImpl implements AdminAttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;
    private final SectionRepository sectionRepository;
    private final SessionRepository sessionRepository;
    private final AttendanceRulesRepository attendanceRulesRepository;
    private final TeacherRepository teacherRepository;

    public AdminAttendanceServiceImpl(AttendanceRepository attendanceRepository, StudentRepository studentRepository, ClassRepository classRepository, SectionRepository sectionRepository, SessionRepository sessionRepository, AttendanceRulesRepository attendanceRulesRepository, TeacherRepository teacherRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.classRepository = classRepository;
        this.sectionRepository = sectionRepository;
        this.sessionRepository = sessionRepository;
        this.attendanceRulesRepository = attendanceRulesRepository;
        this.teacherRepository = teacherRepository;
    }

    @Override
    public String updateStudentsAttendanceRecord(Long attendanceId, Attendance attendance) {
        Attendance existedAttendanceRecord = attendanceRepository.findById(attendanceId).orElseThrow(() ->
                new ResourceNotFoundException("Attendance record with id " + attendanceId +
                        " not found in database"));

        attendance.setSchoolClass(existedAttendanceRecord.getSchoolClass());
        attendance.setSession(existedAttendanceRecord.getSession());
        attendance.setStudent(existedAttendanceRecord.getStudent());
        attendance.setMarkedBy(MarkBy.ADMIN);

        attendanceRepository.save(attendance);

        return "Attendance record with id " + attendanceId + " has updated successfully.";
    }

    @Override
    public Attendance viewAttendanceRecord(Long attendanceId) {
        return attendanceRepository.findById(attendanceId).orElseThrow(() ->
                new ResourceNotFoundException("Attendance record with id " + attendanceId +
                        " not found in database"));
    }

    @Override
    public List<Attendance> viewAllAttendanceRecords(Long sessionId) {
        return attendanceRepository.findAllBySession_SessionId(sessionId);
    }

    @Override
    public String deleteAttendanceRecord(Long attendanceId) {

        Attendance attendance = attendanceRepository.findById(attendanceId).orElseThrow(() ->
                new ResourceNotFoundException("Attendance record with id " + attendanceId +
                        " not found in database"));

        attendanceRepository.delete(attendance);

        return "Attendance record with id " + attendanceId + " has deleted successfully.";
    }

    @Override
    public Resource generatePdfReport(GeneratePdfReportRequest request) throws FileNotFoundException {

        Student student = studentRepository.findByRollNumber(request.getRollNo()).orElseThrow(() ->
                new ResourceNotFoundException("Student record with id " + request.getRollNo() +
                        " not found in database"));

        // As currently there is no mapping established between Student, SchoolClass and Section, so
        // we have to fetch it explicitly from their respective repositories.

        SchoolClass schoolClass = classRepository.findById(student.getSchoolClass().getClassId()).orElseThrow(() ->
                new ResourceNotFoundException("Class record with id " + student.getSchoolClass().getClassId() +
                        " not found in database"));

        Section section = sectionRepository.findById(student.getSection().getSectionId()).orElseThrow(() ->
                new ResourceNotFoundException("Section record with id " + student.getSection().getSectionId() +
                        " not found in database"));


        List<Attendance> attendancesFromToToDate = attendanceRepository.findAllByDateBetween(request.getFromDate(),
                request.getToDate());

        List<Attendance> studentAttendanceRecords = attendancesFromToToDate.stream().filter(attendance ->
                attendance.getStudent().getRollNumber().equals(request.getRollNo())).toList();

        File dir = new File("attendance_reports/pdf");
        if (!dir.exists()) dir.mkdirs();

        String fileName = "attendance_report_" + student.getRollNumber() + "_from_" + request.getFromDate() + "_to_" + request.getToDate() + ".pdf";
        String filePath = "attendance_reports/pdf" + File.separator + fileName;

        File file = new File(filePath);

        PdfWriter writer = new PdfWriter(new FileOutputStream(file));
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Attendance Report")
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(18));

        float[] columnWidths = {
                2,  // Roll No
                4,  // Student Name
                2,  // Class
                2,  // Section
                3,  // Date
                2,  // Session Id
                2,  // Join Time
                2,  // Exit Time
                2,  // Duration Minutes
                2,  // Teacher Id
                4,  // Teacher Name
                2,  // Status
                2   // Mark By
        };

        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100)); // use full page width
        table.setHorizontalAlignment(HorizontalAlignment.CENTER); // center table block
        table.setTextAlignment(TextAlignment.CENTER);
        table.setFontSize(10);

        table.addHeaderCell("Roll No");
        table.addHeaderCell("Student Name");
        table.addHeaderCell("Class");
        table.addHeaderCell("Section");
        table.addHeaderCell("Date");
        table.addHeaderCell("Session Id");
        table.addHeaderCell("Join Time");
        table.addHeaderCell("Exit Time");
        table.addHeaderCell("Duration Minutes");
        table.addHeaderCell("Teacher Id");
        table.addHeaderCell("Teacher Name");
        table.addHeaderCell("Status");
        table.addHeaderCell("Mark By");

        studentAttendanceRecords.forEach(attendance -> {
            if(attendance.getStatus().equals(AttendanceStatus.LEAVE)) {
                table.addCell(student.getRollNumber());
                table.addCell(student.getFirstName() + " " + student.getMiddleName()
                        + " " + student.getLastName());
                table.addCell(schoolClass.getClassName());
                table.addCell(section.getSectionName());
                table.addCell(String.valueOf(attendance.getDate()));
                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell(attendance.getStatus().name());
                table.addCell(attendance.getMarkedBy().name());
            }else {
                table.addCell(student.getRollNumber());
                table.addCell(student.getFirstName() + " " + student.getMiddleName()
                        + " " + student.getLastName());
                table.addCell(schoolClass.getClassName());
                table.addCell(section.getSectionName());
                table.addCell(String.valueOf(attendance.getDate()));
                table.addCell(String.valueOf(attendance.getSession().getSessionId()));
                table.addCell(String.valueOf(attendance.getJoinTime()));
                table.addCell(String.valueOf(attendance.getExitTime()));
                table.addCell(String.valueOf(attendance.getDurationMinutes()));
                table.addCell(String.valueOf(attendance.getSession().getTeacher().getId()));
                table.addCell(attendance.getSession().getTeacher().getFirstName() + " " +
                        attendance.getSession().getTeacher().getLastName());
                table.addCell(attendance.getStatus().name());
                table.addCell(attendance.getMarkedBy().name());
            }
        });

        document.add(table).setFontSize(10);
        document.close();

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return resource;

    }
}

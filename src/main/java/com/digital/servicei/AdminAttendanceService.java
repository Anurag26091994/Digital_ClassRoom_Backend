package com.digital.servicei;

import com.digital.dto.GeneratePdfReportRequest;
import com.digital.entity.Attendance;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.util.List;

public interface AdminAttendanceService {
    String updateStudentsAttendanceRecord(Long attendanceId, Attendance attendance);

    Attendance viewAttendanceRecord(Long attendanceId);

    List<Attendance> viewAllAttendanceRecords(Long sessionId);

    String deleteAttendanceRecord(Long attendanceId);

    Resource generatePdfReport(GeneratePdfReportRequest request) throws FileNotFoundException;
}

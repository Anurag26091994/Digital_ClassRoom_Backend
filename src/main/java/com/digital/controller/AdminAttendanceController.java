package com.digital.controller;

import com.digital.dto.GeneratePdfReportRequest;
import com.digital.entity.Attendance;
import com.digital.servicei.AdminAttendanceService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/attendance/admin")
public class AdminAttendanceController {

    private final AdminAttendanceService adminAttendanceService;

    public AdminAttendanceController(AdminAttendanceService adminAttendanceService) {
        this.adminAttendanceService = adminAttendanceService;
    }

    /**
     * API for ADMIN to update student attendance record by using attendance id
     * */

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(path = "/update/{attendanceId}", consumes = "application/json", produces = "text/plain")
    public ResponseEntity<String> updateStudentsAttendanceRecord(@PathVariable Long attendanceId, @RequestBody Attendance attendance){
        return new ResponseEntity<>(adminAttendanceService.updateStudentsAttendanceRecord(attendanceId, attendance), HttpStatus.OK);
    }

    /**
     * API for ADMIN to view attendance record by attendance id
     * */

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/view/{attendanceId}", produces = "application/json")
    public ResponseEntity<Attendance> viewAttendanceRecord(@PathVariable Long attendanceId){
        return new ResponseEntity<>(adminAttendanceService.viewAttendanceRecord(attendanceId), HttpStatus.OK);
    }

    /**
     * API for ADMIN to view all attendance record
     * */

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/view-all/{sessionId}", produces = "application/json")
    public ResponseEntity<List<Attendance>> viewAllAttendanceRecords(@PathVariable Long sessionId){
        return new ResponseEntity<>(adminAttendanceService.viewAllAttendanceRecords(sessionId), HttpStatus.OK);
    }

    /**
     * API for ADMIN to delete attendance record by attendance id
     * */

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/delete/{attendanceId}")
    public ResponseEntity<String> deleteAttendanceRecord(@PathVariable Long attendanceId){
       String message = adminAttendanceService.deleteAttendanceRecord(attendanceId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    /**
     * API for ADMIN and TEACHER to generate attendance report in pdf format using student roll number,
     * from date and to date.
     * */

    @PreAuthorize("hasAnyRole('ADMIN', TEACHER)")
    @PostMapping(value = "/pdf", consumes = "application/json", produces="application/pdf")
    public ResponseEntity<Resource> generatePdfReport(@RequestBody GeneratePdfReportRequest request) throws FileNotFoundException {
        return new ResponseEntity<>(adminAttendanceService.generatePdfReport(request), HttpStatus.OK);
    }
}

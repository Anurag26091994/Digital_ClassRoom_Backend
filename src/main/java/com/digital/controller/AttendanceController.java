package com.digital.controller;

import com.digital.dto.ViewStudentCheckListResponse;
import com.digital.entity.Attendance;
import com.digital.servicei.AttendanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RequestMapping(value = "/api/attendance")
@RestController
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

     /**
      *  API for Student to join or re-join the session.
      *  If student is joining the session for the first time then new attendance record is created.
      *  If student is rejoining the session then existed attendance record is updated with new joining time.
     */

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping(path = "/join-session/{sessionId}", produces = "text/plain")
    public ResponseEntity<String> joinSession(@PathVariable Long sessionId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<>(attendanceService.joinSession(username, sessionId), HttpStatus.CREATED);
    }

    /**
     *  API for student to leave the session
     *  This api calculate the current duration, last duration and session duration percentage based on
     *  attendance rule and mark the attendance status accordingly.
     * */

    @PreAuthorize("hasRole('STUDENT')")
    @PutMapping(path = "/leave-session/{sessionId}", produces = "text/plain")
    public ResponseEntity<String> leaveSession(@PathVariable Long sessionId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<>(attendanceService.leaveSession(username, sessionId), HttpStatus.CREATED);
    }

    /**
     *  API for teachers to mark students as absent those who have not attended the last session.
     * */

    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping(path = "/auto-mark/{sessionId}", produces = "text/plain")
    public ResponseEntity<String> markStudentsAsAbsent(@PathVariable Long sessionId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<>(attendanceService.markStudentsAsAbsent(username, sessionId), HttpStatus.OK);
    }

    /**
     * API for teachers to view student check-in list using session id.
     * */

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping(path = "/check-in-list/{sessionId}", produces = "application/json")
    public ResponseEntity<List<ViewStudentCheckListResponse>> viewCheckInStudentList(@PathVariable Long sessionId){
        return new ResponseEntity<>(attendanceService.viewCheckInStudentList(sessionId), HttpStatus.OK);
    }

    /**
     * API for teachers to view particular student's attendance record by using attendance id.
     * */

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping(path = "/view/{attendanceId}", produces = "application/json")
    public ResponseEntity<Attendance> viewAttendanceRecord(@PathVariable Long attendanceId){
        return new ResponseEntity<>(attendanceService.viewAttendanceRecord(attendanceId), HttpStatus.OK);
    }

    /**
     * API for teachers to update student attendance record by using attendance id
     * */

    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping(path = "/update/{attendanceId}", consumes = "application/json", produces = "text/plain")
    public ResponseEntity<String> updateStudentsAttendanceRecord(@PathVariable Long attendanceId, @RequestBody Attendance attendance){
        return new ResponseEntity<>(attendanceService.updateStudentsAttendanceRecord(attendanceId, attendance), HttpStatus.OK);
    }

    /**
     * API to view all attendance records by session id
     * */

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping(path = "/view-all/{sessionId}", produces = "application/json")
    public ResponseEntity<List<Attendance>> viewAllAttendanceRecords(@PathVariable Long sessionId){
        return new ResponseEntity<>(attendanceService.viewAllAttendanceRecords(sessionId), HttpStatus.OK);
    }
}

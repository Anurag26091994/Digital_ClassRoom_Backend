package com.digital.servicei;

import com.digital.dto.ViewStudentCheckListResponse;
import com.digital.entity.Attendance;

import java.util.List;

public interface AttendanceService {

    String joinSession(String username, Long sessionId);

    String leaveSession(String username, Long sessionId);

    String markStudentsAsAbsent(String username, Long sessionId);

    List<ViewStudentCheckListResponse> viewCheckInStudentList(Long sessionId);

    String updateStudentsAttendanceRecord(Long attendanceId, Attendance attendance);

    Attendance viewAttendanceRecord(Long attendanceId);

    List<Attendance> viewAllAttendanceRecords(Long sessionId);

}

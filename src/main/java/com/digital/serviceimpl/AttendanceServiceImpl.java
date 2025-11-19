package com.digital.serviceimpl;

import com.digital.dto.ViewStudentCheckListResponse;
import com.digital.entity.*;
import com.digital.enums.AttendanceRuleName;
import com.digital.enums.AttendanceStatus;
import com.digital.enums.MarkBy;
import com.digital.exception.ResourceNotFoundException;
import com.digital.repository.*;
import com.digital.servicei.AttendanceService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final SessionRepository sessionRepository;
    private final AttendanceRulesRepository attendanceRulesRepository;
    private final TeacherRepository teacherRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository, StudentRepository studentRepository, SessionRepository sessionRepository, AttendanceRulesRepository attendanceRulesRepository, TeacherRepository teacherRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.sessionRepository = sessionRepository;
        this.attendanceRulesRepository = attendanceRulesRepository;
        this.teacherRepository = teacherRepository;
    }

    @Override
    public String joinSession(String username, Long sessionId) {

        Student student = studentRepository.findByUser_Username(username).orElseThrow(() ->
                new ResourceNotFoundException("Student record with username " + username + " not found in database."));

        Session session = sessionRepository.findById(sessionId).orElseThrow(() ->
                new ResourceNotFoundException("Session record with session id " + sessionId + " not found in database."));

        SchoolClass schoolClass = session.getSchoolClass();

        // checking if student try to join the session before or after the session date

        if(LocalDate.now().isBefore(session.getDate()) || LocalDate.now().isAfter(session.getDate()))
            return "Session is scheduled on date " + session.getDate() + "\n Students can join the session only on the session date";

        // checking if student try to re-join the session

        if (attendanceRepository.findByStudent_StudentRegIdAndSession_SessionId(student.getStudentRegId(),
                session.getSessionId()).isPresent()) {

            Attendance existedAttendanceRecord = attendanceRepository.findByStudent_StudentRegIdAndSession_SessionId(student.getStudentRegId(),
                            session.getSessionId()).orElseThrow(() -> new ResourceNotFoundException("Attendance record with student id: "
                    + student.getStudentRegId() + " and session id: " + session.getSessionId() + " not found in database."));

            existedAttendanceRecord.setJoinTime(LocalDateTime.now());
            attendanceRepository.save(existedAttendanceRecord);

            return student.getFirstName() + " " + student.getLastName() + " has rejoined the session at " +
                    existedAttendanceRecord.getJoinTime();
        }

        // checking if student try to join the session after session end time

        if (LocalDateTime.now().isAfter(session.getEndTime()))
                return "Session has ended at " + session.getEndTime();

        /*
         checking if student try to join the session after the 15 minutes of session start time.
         Here we are fetching the attendance rule for LATE check-in, to get value of late minutes.
         sessionDurationPercentage1 is the value of late minutes.
         If we want to change the late minutes value we can change it by updating the LATE rule.
        */

        AttendanceRule attendanceRule = attendanceRulesRepository.findByRuleName(AttendanceRuleName.LATE).orElseThrow(() ->
                new ResourceNotFoundException("No rule found for LATE check-in"));

        if(LocalDateTime.now().isAfter(session.getStartTime().plusMinutes(attendanceRule.getSessionDurationPercentage1()))){

            Attendance attendance = Attendance.builder()
                    .student(student)
                    .session(session)
                    .schoolClass(schoolClass)
                    .date(LocalDate.now())
                    .joinTime(LocalDateTime.now())
                    .status(AttendanceStatus.LATE)
                    .build();

            attendanceRepository.save(attendance);

            return student.getFirstName() + " " + student.getLastName() + " has joined the session: " + session.getTopic()
                   + " at " + attendance.getJoinTime() + " and mark as LATE";
        }

        Attendance attendance = Attendance.builder()
                .student(student)
                .session(session)
                .schoolClass(schoolClass)
                .date(LocalDate.now())
                .joinTime(LocalDateTime.now())
                .build();

        /* If student has joined the session before session start time then his/her joined time will be
        session start time */

        if(LocalDateTime.now().isBefore(session.getStartTime())){
            attendance.setJoinTime(session.getStartTime());
        }

        attendance.setMarkedBy(MarkBy.SYSTEM);

        attendanceRepository.save(attendance);

        return student.getFirstName() + " " + student.getLastName() + " has check-in and joined the session: " + session.getTopic() + " at " + attendance.getJoinTime();
    }

    @Override
    public String leaveSession(String username, Long sessionId) {

        Student student = studentRepository.findByUser_Username(username).orElseThrow(() ->
                new ResourceNotFoundException("Student record with username " + username + " not found in database."));

        Session session = sessionRepository.findById(sessionId).orElseThrow(() ->
                new ResourceNotFoundException("Session record with session id " + sessionId + " not found in database."));

        SchoolClass schoolClass = session.getSchoolClass();

        // fetching the attendance record based on student id and session id
        Attendance existedAttendanceRecord = attendanceRepository.findByStudent_StudentRegIdAndSession_SessionId(student.getStudentRegId(),
                session.getSessionId()).orElseThrow(() -> new ResourceNotFoundException("Attendance record with student id: "
                + student.getStudentRegId() + " and session id: " + session.getSessionId() + " not found in database."));

        LocalDateTime exitTime = LocalDateTime.now();

        existedAttendanceRecord.setExitTime(exitTime);

        // if student exit time is after the session end time then setting exit time as session end time
        if(exitTime.isAfter(session.getEndTime())){
            existedAttendanceRecord.setExitTime(session.getEndTime());
        }

        // calculating the student session duration in minutes using session student joined time and exit time
        long currentDurationMinutes = Duration.between(existedAttendanceRecord.getJoinTime(),
                existedAttendanceRecord.getExitTime()).toMinutes();

        System.out.println("Current Duration Minutes: " + currentDurationMinutes);

        // if student has re-joined then last duration minutes will be added to current duration minutes
        Long lastDurationMinutes = existedAttendanceRecord.getDurationMinutes();

        System.out.println("Last Duration Minutes: " + lastDurationMinutes);

        if(lastDurationMinutes!=null)
            existedAttendanceRecord.setDurationMinutes(lastDurationMinutes + currentDurationMinutes);
        else
            existedAttendanceRecord.setDurationMinutes(currentDurationMinutes);

        System.out.println("Updated Duration Minutes: " + existedAttendanceRecord.getDurationMinutes());

        // calculating pre-defined session duration
        long predefinedSessionDurationMinutes = Duration.between(session.getStartTime(), session.getEndTime()).toMinutes();

        System.out.println("predefinedSessionDurationMinutes " + predefinedSessionDurationMinutes);

        // calculating student session duration percentage
        double actualSessionDurationPercentage = (double)((existedAttendanceRecord.getDurationMinutes() * 100) / predefinedSessionDurationMinutes);

        System.out.println("actual Session Duration Percentage: " + actualSessionDurationPercentage);

        /* fetching attendance rules from database, set by ADMIN, to set Attendance status based on
           student attendance percentage and attendance rule percentage */
        AttendanceRule presentAttendanceRule = attendanceRulesRepository.findByRuleName(AttendanceRuleName.PRESENT).orElseThrow(() ->
                new ResourceNotFoundException("No rule found for PRESENT status"));

        AttendanceRule absentAttendanceRule = attendanceRulesRepository.findByRuleName(AttendanceRuleName.ABSENT).orElseThrow(() ->
                new ResourceNotFoundException("No rule found for ABSENT status"));

        AttendanceRule halfDayAttendanceRule = attendanceRulesRepository.findByRuleName(AttendanceRuleName.HALF_DAY).orElseThrow(() ->
                new ResourceNotFoundException("No rule found for HALF-DAY status"));

        if(actualSessionDurationPercentage >= presentAttendanceRule.getSessionDurationPercentage1())
            existedAttendanceRecord.setStatus(AttendanceStatus.PRESENT);

        if(actualSessionDurationPercentage < absentAttendanceRule.getSessionDurationPercentage1())
            existedAttendanceRecord.setStatus(AttendanceStatus.ABSENT);

        if((actualSessionDurationPercentage >= halfDayAttendanceRule.getSessionDurationPercentage1()) &&
                (actualSessionDurationPercentage < halfDayAttendanceRule.getSessionDurationPercentage2()))
            existedAttendanceRecord.setStatus(AttendanceStatus.HALF_DAY);

        existedAttendanceRecord.setMarkedBy(MarkBy.SYSTEM);

        attendanceRepository.save(existedAttendanceRecord);

        return student.getFirstName() + " " + student.getLastName() + " has check-out and leaved the session: " + session.getTopic() +  " at " + existedAttendanceRecord.getJoinTime() + " and marked has " + existedAttendanceRecord.getStatus().name();
    }

    @Override
    public String markStudentsAsAbsent(String username, Long sessionId) {

        Teacher teacher = teacherRepository.findByUser_Username(username).orElseThrow(() -> new ResourceNotFoundException("Teacher record with username " + username + " not found in database."));

        Session session = sessionRepository.findById(sessionId).orElseThrow(() ->
                new ResourceNotFoundException("Session record with session id " + sessionId + " not found in database."));

        if(!LocalDateTime.now().isAfter(session.getEndTime())){
            return "Session " + sessionId + " is not ended yet.";
        }

        Long classId = session.getSchoolClass().getClassId();
        Long sectionId = session.getSection().getSectionId();

        // As there is no mapping available between Student, SchoolClass and Section in Student class, so we are using
        // following method
        List<Student> allByClassIdAndSectionId = studentRepository.findAllBySchoolClass_ClassIdAndSection_SectionId(classId, sectionId);

        AtomicInteger absentStudentCount = new AtomicInteger();

        allByClassIdAndSectionId.forEach(student -> {

            if(!attendanceRepository.existsByStudent_StudentRegIdAndSession_SessionId(
                    student.getStudentRegId(),
                    sessionId)){

                absentStudentCount.incrementAndGet();

                Attendance attendance = Attendance.builder()
                        .date(LocalDate.now())
                        .schoolClass(session.getSchoolClass())
                        .student(student)
                        .session(session)
                        .status(AttendanceStatus.ABSENT)
                        .markedBy(MarkBy.TEACHER)
                        .build();
                attendanceRepository.save(attendance);
            }
        });

        return absentStudentCount.get() + " students are marked as ABSENT";
    }

    @Override
    public List<ViewStudentCheckListResponse> viewCheckInStudentList(Long sessionId) {

        Session session = sessionRepository.findById(sessionId).orElseThrow(() ->
                new ResourceNotFoundException("Session record with id " + sessionId + " not present in database"));

        List<ViewStudentCheckListResponse> viewList = new ArrayList<>();

        List<Attendance> attendances = attendanceRepository.findAllBySession_SessionId(sessionId);

        attendances.forEach(attendance -> {

            ViewStudentCheckListResponse viewStudentCheckListResponse = ViewStudentCheckListResponse.builder()
                    .studentId(attendance.getStudent().getStudentRegId())
                    .rollNo(attendance.getStudent().getRollNumber())
                    .fullName(attendance.getStudent().getFirstName() + " " + attendance.getStudent()
                            .getMiddleName() + " " + attendance.getStudent().getLastName())
                    .joinTime(attendance.getJoinTime())
                    .exitTime(attendance.getExitTime())
                    .durationMinutes(attendance.getDurationMinutes())
                    .sessionId(sessionId)
                    .sessionTopic(session.getTopic())
                    .teacherId(attendance.getSession().getTeacher().getId())
                    .teacherName(attendance.getSession().getTeacher().getFirstName() + " "
                            + attendance.getSession().getTeacher().getLastName())
                    .attendanceStatus(attendance.getStatus())
                    .markBy(attendance.getMarkedBy())
                    .build();

            viewList.add(viewStudentCheckListResponse);
        });

        return viewList;
    }

    @Override
    public String updateStudentsAttendanceRecord(Long attendanceId, Attendance attendance) {

        Attendance existedAttendanceRecord = attendanceRepository.findById(attendanceId).orElseThrow(() ->
                new ResourceNotFoundException("Attendance record with id " + attendanceId +
                        " not found in database"));

        attendance.setSchoolClass(existedAttendanceRecord.getSchoolClass());
        attendance.setSession(existedAttendanceRecord.getSession());
        attendance.setStudent(existedAttendanceRecord.getStudent());
        attendance.setMarkedBy(MarkBy.TEACHER);

        attendanceRepository.save(attendance);

        return "Attendance record with id " + attendanceId + " has updated successfully.";
    }

    @Override
    public Attendance viewAttendanceRecord(Long attendanceId) {
        return attendanceRepository.findById(attendanceId).orElseThrow(() ->
                new ResourceNotFoundException("Attendance record with id " + attendanceId + " not found in database"));
    }

    @Override
    public List<Attendance> viewAllAttendanceRecords(Long sessionId) {
        return attendanceRepository.findAllBySession_SessionId(sessionId);
    }

}

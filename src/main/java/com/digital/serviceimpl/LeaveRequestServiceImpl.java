package com.digital.serviceimpl;

import com.digital.dto.LeaveRequestDto;
import com.digital.dto.MakeLeaveRequest;
import com.digital.entity.*;
import com.digital.enums.AttendanceStatus;
import com.digital.enums.LeaveRequestStatus;
import com.digital.enums.MarkBy;
import com.digital.enums.Role;
import com.digital.events.LeaveApprovalEvent;
import com.digital.events.TeacherOnLeaveEvent;
import com.digital.exception.CustomUnauthorizedException;
import com.digital.exception.LeaveOverlappingException;
import com.digital.exception.ResourceNotFoundException;
import com.digital.repository.*;
import com.digital.servicei.LeaveRequestService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final LeaveAttendanceSyncRepository leaveAttendanceSyncRepository;
    private final ApplicationEventPublisher publisher;

    /* This api is for both STUDENT and TEACHER. Both can apply leave using this api. */

    @Override
    public LeaveRequestDto applyForLeave(MakeLeaveRequest makeLeaveRequest, String username) {

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("User with username: " + username
                        + " not found in database."));

        // checking leaves are overlap or not.

        if(leaveRequestRepository.existsByUser_UserIdAndFromDateLessThanEqualAndToDateGreaterThanEqual(
                user.getUserId(), makeLeaveRequest.getFromDate(), makeLeaveRequest.getToDate())){

            throw new LeaveOverlappingException("Leave overlapped! You have already applied for the leave"
                    + " from " + makeLeaveRequest.getFromDate() + " to " + makeLeaveRequest.getToDate());
        }

        /* If approvedByTeacher is not null that means leave request is applied by STUDENT because STUDENT
           leave is approved/rejected by TEACHER and if approvedByTeacher is null means leave request is
           applied by TEACHER because TEACHER leave requests are approved/rejected by ADMIN.
        */

        if(makeLeaveRequest.getApprovedByTeacher() != null) {

            Teacher teacher = teacherRepository.findById(makeLeaveRequest.getApprovedByTeacher().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher with id: " +
                            makeLeaveRequest.getApprovedByTeacher().getId() + " not found in database."));

            LeaveRequest leaveRequest = LeaveRequest.builder()
                    .user(user)
                    .leaveType(makeLeaveRequest.getLeaveType())
                    .fromDate(makeLeaveRequest.getFromDate())
                    .toDate(makeLeaveRequest.getToDate())
                    .approvedByTeacher(teacher)
                    .reason(makeLeaveRequest.getReason())
                    .appliedOn(LocalDate.now())
                    .status(LeaveRequestStatus.Pending)
                    .build();

            LeaveRequest savedLeaverequest = leaveRequestRepository.save(leaveRequest);

            return mapToLeaveRequestDtoForStudent(savedLeaverequest);
        } else{

            Admin admin = adminRepository.findAll().get(0);

            LeaveRequest leaveRequest = LeaveRequest.builder()
                    .user(user)
                    .leaveType(makeLeaveRequest.getLeaveType())
                    .fromDate(makeLeaveRequest.getFromDate())
                    .toDate(makeLeaveRequest.getToDate())
                    .approvedByAdmin(admin)
                    .reason(makeLeaveRequest.getReason())
                    .appliedOn(LocalDate.now())
                    .status(LeaveRequestStatus.Pending)
                    .build();

            LeaveRequest savedLeaverequest = leaveRequestRepository.save(leaveRequest);

            return mapToLeaveRequestDtoForTeacher(savedLeaverequest);
        }
    }

    @Override
    public List<LeaveRequestDto> viewLeaveApprovalStatus(String username) {

        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException("User with " + "username: " + username
                        + " not found in database."));

        List<LeaveRequest> leaveRequests = leaveRequestRepository.findAllByUser_UserIdAndStatus(user.getUserId(),
                LeaveRequestStatus.Pending);

        return leaveRequests.stream().map(this::mapToLeaveRequestDtoForStudent).toList();
    }

    @Override
    public LeaveRequestDto approveStudentLeaveRequest(String username, Long leaveRequestId) {

        /* If optionalTeacher is empty means this api is currently accessed by ADMIN and if optionalTeacher
           is not empty means this api si currently access by TEACHER.
           This api can only be accessed by TEACHER and ADMIN.
           Why ADMIN because if some leave requests are escalated then in that case ADMIn will access
           this api.
        */

        Optional<Teacher> optionalTeacher = teacherRepository.findByUser_Username(username);

        if(optionalTeacher.isEmpty()){

            Admin admin = adminRepository.findAll().get(0);

            LeaveRequest existingLeaveRequest = leaveRequestRepository.findById(leaveRequestId).orElseThrow(() ->
                    new ResourceNotFoundException("Leave request with id: " + leaveRequestId +
                            " not found in database"));

            existingLeaveRequest.setStatus(LeaveRequestStatus.Approved);
            existingLeaveRequest.setApprovalDate(LocalDate.now());

            existingLeaveRequest.setApprovedByAdmin(admin);

            LeaveRequest savedLeaveRequest = leaveRequestRepository.save(existingLeaveRequest);

            publisher.publishEvent(new LeaveApprovalEvent(savedLeaveRequest.getLeaveId(),
                    savedLeaveRequest.getFromDate(), savedLeaveRequest.getToDate(),
                    savedLeaveRequest.getStatus().name(), savedLeaveRequest.getUser().getUserId()));

            Student student = studentRepository.findByUser_UserId(savedLeaveRequest.getUser().getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student record with user id: "
                            + savedLeaveRequest.getUser().getUserId() + " not found in database"));

            /* We are counting days because if leave is for 3 days then 3 Attendance record and
               3 LeaveAttendanceSync records will be created.
            */

            long days = ChronoUnit.DAYS.between(savedLeaveRequest.getFromDate(),
                    savedLeaveRequest.getToDate()) + 1;

            for (long i = 0; i < days; i++) {

                Attendance attendance = Attendance.builder()
                        .student(student)
                        .schoolClass(student.getSchoolClass())
                        .date(savedLeaveRequest.getFromDate().plusDays(i))
                        .status(AttendanceStatus.LEAVE)
                        .markedBy(MarkBy.SYSTEM)
                        .build();
                Attendance savedAttendance = attendanceRepository.save(attendance);

                LeaveAttendanceSync leaveAttendanceSync = LeaveAttendanceSync.builder()
                        .attendance(savedAttendance)
                        .leaveRequest(savedLeaveRequest)
                        .date(savedLeaveRequest.getFromDate().plusDays(i))
                        .attendanceStatus(AttendanceStatus.LEAVE)
                        .build();

                leaveAttendanceSyncRepository.save(leaveAttendanceSync);
            }
                return mapToLeaveRequestDtoForTeacher(savedLeaveRequest);
        }
        else {
            Teacher teacher = optionalTeacher.get();

            if (teacher.isAssignedAsClassTeacher()) {

                LeaveRequest existingLeaveRequest = leaveRequestRepository.findById(leaveRequestId).orElseThrow(() ->
                        new ResourceNotFoundException("Leave request with id: " + leaveRequestId +
                                " not found in database"));

                existingLeaveRequest.setStatus(LeaveRequestStatus.Approved);
                existingLeaveRequest.setApprovalDate(LocalDate.now());

                LeaveRequest savedLeaveRequest = leaveRequestRepository.save(existingLeaveRequest);

                publisher.publishEvent(new LeaveApprovalEvent(savedLeaveRequest.getLeaveId(),
                        savedLeaveRequest.getFromDate(), savedLeaveRequest.getToDate(),
                        savedLeaveRequest.getStatus().name(), savedLeaveRequest.getUser().getUserId()));

                Student student = studentRepository.findByUser_UserId(savedLeaveRequest.getUser().getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("Student record with user id: "
                                + savedLeaveRequest.getUser().getUserId() + " not found in database"));

                long days = ChronoUnit.DAYS.between(savedLeaveRequest.getFromDate(),
                        savedLeaveRequest.getToDate()) + 1;

                for (long i = 0; i < days; i++) {

                    Attendance attendance = Attendance.builder()
                            .student(student)
                            .schoolClass(student.getSchoolClass())
                            .date(savedLeaveRequest.getFromDate().plusDays(i))
                            .status(AttendanceStatus.LEAVE)
                            .markedBy(MarkBy.SYSTEM)
                            .build();
                    Attendance savedAttendance = attendanceRepository.save(attendance);

                    LeaveAttendanceSync leaveAttendanceSync = LeaveAttendanceSync.builder()
                            .attendance(savedAttendance)
                            .leaveRequest(savedLeaveRequest)
                            .date(savedLeaveRequest.getFromDate().plusDays(i))
                            .attendanceStatus(AttendanceStatus.LEAVE)
                            .build();

                    leaveAttendanceSyncRepository.save(leaveAttendanceSync);
                }
                return mapToLeaveRequestDtoForStudent(savedLeaveRequest);
            } else {
                throw new CustomUnauthorizedException("Teacher with id: " + teacher.getId() +
                        " is not authorized to perform approve leave request");
            }
        }
    }

    @Override
    public LeaveRequestDto rejectStudentLeaveRequest(String username, Long leaveRequestId, String remarks) {

        /* If optionalTeacher is empty means this api is currently accessed by ADMIN and if optionalTeacher
           is not empty means this api si currently access by TEACHER.
           This api can only be accessed by TEACHER and ADMIN.
           Why ADMIN because if some leave requests are escalated then in that case ADMIn will access
           this api.
        */

        Optional<Teacher> optionalTeacher = teacherRepository.findByUser_Username(username);

        if(optionalTeacher.isEmpty()){

            Admin admin = adminRepository.findAll().get(0);

            LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId).orElseThrow(() ->
                    new ResourceNotFoundException("Leave request with id: " + leaveRequestId
                            + " not found in database"));

            if (leaveRequest.getStatus().equals(LeaveRequestStatus.Pending) |
                    leaveRequest.getStatus().equals(LeaveRequestStatus.Rejected)) {

                leaveRequest.setStatus(LeaveRequestStatus.Rejected);
                leaveRequest.setRemarks(remarks);

                leaveRequest.setApprovedByAdmin(admin);

                LeaveRequest savedLeaveRequest = leaveRequestRepository.save(leaveRequest);

                publisher.publishEvent(new LeaveApprovalEvent(savedLeaveRequest.getLeaveId(),
                        savedLeaveRequest.getFromDate(), savedLeaveRequest.getToDate(),
                        savedLeaveRequest.getStatus().name(), savedLeaveRequest.getUser().getUserId()));

                return mapToLeaveRequestDtoForTeacher(savedLeaveRequest);
            } else {

                leaveRequest.setStatus(LeaveRequestStatus.Rejected);
                leaveRequest.setRemarks(remarks);

                LeaveRequest savedLeaveRequest = leaveRequestRepository.save(leaveRequest);

                publisher.publishEvent(new LeaveApprovalEvent(savedLeaveRequest.getLeaveId(),
                        savedLeaveRequest.getFromDate(), savedLeaveRequest.getToDate(),
                        savedLeaveRequest.getStatus().name(), savedLeaveRequest.getUser().getUserId()));

                List<LeaveAttendanceSync> leaveAttendanceSyncs = leaveAttendanceSyncRepository
                        .findAllByLeaveRequest_LeaveId(savedLeaveRequest.getLeaveId());

                leaveAttendanceSyncRepository.deleteAll(leaveAttendanceSyncs);

                return mapToLeaveRequestDtoForTeacher(savedLeaveRequest);
            }
        }
        else {
            Teacher teacher = optionalTeacher.get();

            if (teacher.isAssignedAsClassTeacher()) {

                LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId).orElseThrow(() ->
                        new ResourceNotFoundException("Leave request with id: " + leaveRequestId
                                + " not found in database"));

                if (leaveRequest.getStatus().equals(LeaveRequestStatus.Pending) |
                        leaveRequest.getStatus().equals(LeaveRequestStatus.Rejected)) {

                    leaveRequest.setStatus(LeaveRequestStatus.Rejected);
                    leaveRequest.setRemarks(remarks);

                    LeaveRequest savedLeaveRequest = leaveRequestRepository.save(leaveRequest);

                    publisher.publishEvent(new LeaveApprovalEvent(savedLeaveRequest.getLeaveId(),
                            savedLeaveRequest.getFromDate(), savedLeaveRequest.getToDate(),
                            savedLeaveRequest.getStatus().name(), savedLeaveRequest.getUser().getUserId()));

                    return mapToLeaveRequestDtoForStudent(savedLeaveRequest);
                } else {

                    leaveRequest.setStatus(LeaveRequestStatus.Rejected);
                    leaveRequest.setRemarks(remarks);

                    LeaveRequest savedLeaveRequest = leaveRequestRepository.save(leaveRequest);

                    publisher.publishEvent(new LeaveApprovalEvent(savedLeaveRequest.getLeaveId(),
                            savedLeaveRequest.getFromDate(), savedLeaveRequest.getToDate(),
                            savedLeaveRequest.getStatus().name(), savedLeaveRequest.getUser().getUserId()));

                    List<LeaveAttendanceSync> leaveAttendanceSyncs = leaveAttendanceSyncRepository
                            .findAllByLeaveRequest_LeaveId(savedLeaveRequest.getLeaveId());

                    leaveAttendanceSyncRepository.deleteAll(leaveAttendanceSyncs);

                    return mapToLeaveRequestDtoForStudent(savedLeaveRequest);
                }
            } else {
                throw new CustomUnauthorizedException("Teacher with id: " + teacher.getId() +
                        " is not authorized to perform approve leave request");
            }
        }
    }

    @Override
    public List<LeaveRequestDto> viewAllPendingLeaveRequests() {

        List<LeaveRequest> leaveRequests = leaveRequestRepository.findAllByApprovedByAdmin_RoleAndStatus(Role.ADMIN,
                LeaveRequestStatus.Pending);


        return leaveRequests.stream().map(this::mapToLeaveRequestDtoForTeacher).toList();
    }

    @Override
    public LeaveRequestDto approveTeacherLeaveRequest(Long leaveRequestId) {

        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId).orElseThrow(() ->
                new ResourceNotFoundException("Leave request with id: " + leaveRequestId
                        + " not found in database"));

        leaveRequest.setStatus(LeaveRequestStatus.Approved);
        leaveRequest.setApprovalDate(LocalDate.now());

        LeaveRequest savedLeaveRequest = leaveRequestRepository.save(leaveRequest);

        Teacher teacher = teacherRepository.findByUser_UserId(savedLeaveRequest.getUser().getUserId());

        List<Long> schoolClassIds = teacher.getAssignedClass().stream().map(SchoolClass::getClassId).toList();


        publisher.publishEvent(new LeaveApprovalEvent(savedLeaveRequest.getLeaveId(),
                savedLeaveRequest.getFromDate(), savedLeaveRequest.getToDate(),
                savedLeaveRequest.getStatus().name(), savedLeaveRequest.getUser().getUserId()));

        publisher.publishEvent(new TeacherOnLeaveEvent(savedLeaveRequest.getApprovedByAdmin().getAdminId(),
                teacher.getId(), savedLeaveRequest.getLeaveId(), savedLeaveRequest.getFromDate(),
                savedLeaveRequest.getToDate(), schoolClassIds));

        return mapToLeaveRequestDtoForTeacher(savedLeaveRequest);
    }

    @Override
    public LeaveRequestDto rejectTeacherLeaveRequest(Long leaveRequestId, String remarks) {

        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId).orElseThrow(() ->
                new ResourceNotFoundException("Leave request with id: " + leaveRequestId
                        + " not found in database"));

        leaveRequest.setStatus(LeaveRequestStatus.Rejected);
        leaveRequest.setRemarks(remarks);

        LeaveRequest savedLeaveRequest = leaveRequestRepository.save(leaveRequest);

        publisher.publishEvent(new LeaveApprovalEvent(savedLeaveRequest.getLeaveId(),
                savedLeaveRequest.getFromDate(), savedLeaveRequest.getToDate(),
                savedLeaveRequest.getStatus().name(), savedLeaveRequest.getUser().getUserId()));

        return mapToLeaveRequestDtoForTeacher(savedLeaveRequest);
    }

    @Override
    public List<LeaveRequestDto> viewStudentPendingLeaveRequests(String username) {

        /* If optionalTeacher is empty means this api is currently accessed by ADMIN and if optionalTeacher
           is not empty means this api si currently access by TEACHER.
           This api can only be accessed by TEACHER and ADMIN.
           Why ADMIN because if some leave requests are escalated then in that case ADMIN will access
           this api.
        */

        Optional<Teacher> optionalTeacher = teacherRepository.findByUser_Username(username);

        if(optionalTeacher.isEmpty()){

            //Admin admin = adminRepository.findAll().get(0);

            List<LeaveRequest> leaveRequests = leaveRequestRepository.findAllByStatus(LeaveRequestStatus.Pending);

            return leaveRequests.stream().map(this::mapToLeaveRequestDtoForStudent).toList();
        }
        else {
            Teacher teacher = optionalTeacher.get();

            if (teacher.isAssignedAsClassTeacher()) {

                List<LeaveRequest> leaveRequests = leaveRequestRepository.findAllByApprovedByTeacherAndStatus(teacher,
                        LeaveRequestStatus.Pending);
                return leaveRequests.stream().map(this::mapToLeaveRequestDtoForStudent).toList();
            } else {

                throw new CustomUnauthorizedException("Teacher with id: " + teacher.getId() + " is not authorized to "
                        + "view students pending leave requests");
            }
        }
    }

    LeaveRequestDto mapToLeaveRequestDtoForStudent(LeaveRequest leaveRequest){

        return LeaveRequestDto.builder()
                    .leaveId(leaveRequest.getLeaveId())
                    .userId(leaveRequest.getUser().getUserId())
                    .leaveType(leaveRequest.getLeaveType())
                    .fromDate(leaveRequest.getFromDate())
                    .toDate(leaveRequest.getToDate())
                    .reason(leaveRequest.getReason())
                    .status(leaveRequest.getStatus())
                    .appliedOn(leaveRequest.getAppliedOn())
                    .approvedByTeacher(leaveRequest.getApprovedByTeacher().getId())
                    .classTeacherName(leaveRequest.getApprovedByTeacher().getFirstName() + " "
                            + leaveRequest.getApprovedByTeacher().getLastName())
                    .approvalDate(leaveRequest.getApprovalDate())
                    .remarks(leaveRequest.getRemarks())
                    .build();
    }

    LeaveRequestDto mapToLeaveRequestDtoForTeacher(LeaveRequest leaveRequest){

        return LeaveRequestDto.builder()
                .leaveId(leaveRequest.getLeaveId())
                .userId(leaveRequest.getUser().getUserId())
                .leaveType(leaveRequest.getLeaveType())
                .fromDate(leaveRequest.getFromDate())
                .toDate(leaveRequest.getToDate())
                .reason(leaveRequest.getReason())
                .status(leaveRequest.getStatus())
                .appliedOn(leaveRequest.getAppliedOn())
                .approvedByAdmin(leaveRequest.getApprovedByAdmin().getAdminId())
                .approvalDate(leaveRequest.getApprovalDate())
                .remarks(leaveRequest.getRemarks())
                .build();
    }
}

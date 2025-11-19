package com.digital.serviceimpl;

import com.digital.dto.LeaveRequestDto;
import com.digital.dto.MakeLeaveRequest;
import com.digital.entity.*;
import com.digital.enums.*;
import com.digital.events.LeaveApprovalEvent;
import com.digital.exception.CustomForbiddenException;
import com.digital.exception.CustomUnauthorizedException;
import com.digital.exception.LeaveOverlappingException;
import com.digital.exception.ResourceNotFoundException;
import com.digital.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveRequestServiceImplTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private LeaveAttendanceSyncRepository leaveAttendanceSyncRepository;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private LeaveRequestServiceImpl leaveRequestServiceImpl;

    LocalDate fromDate = LocalDate.now().plusDays(1);
    LocalDate toDate = fromDate.plusDays(1);

    @Test
    void applyForLeave_TestStudent() {

        Teacher teacher = Teacher.builder().id(1L).build();

        MakeLeaveRequest makeLeaveRequest = MakeLeaveRequest.builder()
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .approvedByTeacher(teacher)
                .reason("Suffering from typhoid")
                .build();

        User user = User.builder().userId(1L).username("student1").build();

        when(userRepository.findByUsername("student1")).thenReturn(Optional.of(user));

        when(leaveRequestRepository.existsByUser_UserIdAndFromDateLessThanEqualAndToDateGreaterThanEqual(user.getUserId(),
                makeLeaveRequest.getFromDate(), makeLeaveRequest.getToDate())).thenReturn(false);
        
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .leaveId(1L)
                .user(user)
                .leaveType(makeLeaveRequest.getLeaveType())
                .fromDate(makeLeaveRequest.getFromDate())
                .toDate(makeLeaveRequest.getToDate())
                .approvedByTeacher(teacher)
                .reason(makeLeaveRequest.getReason())
                .appliedOn(LocalDate.now())
                .status(LeaveRequestStatus.Pending)
                .build();

        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequest);

        LeaveRequestDto result = leaveRequestServiceImpl.applyForLeave(makeLeaveRequest, "student1");

        assertNotNull(result);
        assertEquals(LeaveRequestStatus.Pending, result.getStatus());
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(leaveRequestRepository, times(1))
                .existsByUser_UserIdAndFromDateLessThanEqualAndToDateGreaterThanEqual(anyLong(), any(LocalDate.class), any(LocalDate.class));
        verify(teacherRepository, times(1)).findById(anyLong());
        verify(leaveRequestRepository, times(1)).save(any(LeaveRequest.class));
        verify(adminRepository, never()).findAll();
    }

    @Test
    void applyForLeave_TestTeacher() {

        Admin admin = Admin.builder().adminId(1L).username("admin@school.com").build();

        MakeLeaveRequest makeLeaveRequest = MakeLeaveRequest.builder()
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .approvedByAdmin(admin)
                .reason("Suffering from typhoid")
                .build();

        User user = User.builder().userId(1L).username("teacher1").build();

        when(userRepository.findByUsername("teacher1")).thenReturn(Optional.of(user));

        when(leaveRequestRepository.existsByUser_UserIdAndFromDateLessThanEqualAndToDateGreaterThanEqual(user.getUserId(),
                makeLeaveRequest.getFromDate(), makeLeaveRequest.getToDate())).thenReturn(false);

        List<Admin> admins = List.of(admin);

        when(adminRepository.findAll()).thenReturn(admins);

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .leaveId(1L)
                .user(user)
                .leaveType(makeLeaveRequest.getLeaveType())
                .fromDate(makeLeaveRequest.getFromDate())
                .toDate(makeLeaveRequest.getToDate())
                .approvedByAdmin(admin)
                .reason(makeLeaveRequest.getReason())
                .appliedOn(LocalDate.now())
                .status(LeaveRequestStatus.Pending)
                .build();

        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequest);

        LeaveRequestDto result = leaveRequestServiceImpl.applyForLeave(makeLeaveRequest, "teacher1");

        assertNotNull(result);
        assertEquals(LeaveRequestStatus.Pending, result.getStatus());
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(leaveRequestRepository, times(1))
                .existsByUser_UserIdAndFromDateLessThanEqualAndToDateGreaterThanEqual(anyLong(), any(LocalDate.class), any(LocalDate.class));
        verify(teacherRepository, never()).findById(anyLong());
        verify(leaveRequestRepository, times(1)).save(any(LeaveRequest.class));
        verify(adminRepository, times(1)).findAll();
    }

    @Test
    void applyForLeave_ResourceNotFoundException_WhenUserNotFound(){

        Teacher teacher = Teacher.builder().id(1L).build();

        MakeLeaveRequest makeLeaveRequest = MakeLeaveRequest.builder()
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .approvedByTeacher(teacher)
                .reason("Suffering from typhoid")
                .build();

        when(userRepository.findByUsername("student1")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> leaveRequestServiceImpl.applyForLeave(makeLeaveRequest, "student1"));

        assertEquals("User with username: " + "student1" + " not found in database.", exception.getMessage());

        verify(leaveRequestRepository, never())
                .existsByUser_UserIdAndFromDateLessThanEqualAndToDateGreaterThanEqual(anyLong(), any(LocalDate.class), any(LocalDate.class));
        verify(teacherRepository, never()).findById(anyLong());
        verify(leaveRequestRepository, never()).save(any(LeaveRequest.class));
        verify(adminRepository, never()).findAll();
    }

    @Test
    void applyForLeave_LeaveOverlappingException(){
        Teacher teacher = Teacher.builder().id(1L).build();

        MakeLeaveRequest makeLeaveRequest = MakeLeaveRequest.builder()
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .approvedByTeacher(teacher)
                .reason("Suffering from typhoid")
                .build();

        User user = User.builder().userId(1L).username("student1").build();

        when(userRepository.findByUsername("student1")).thenReturn(Optional.of(user));

        when(leaveRequestRepository.existsByUser_UserIdAndFromDateLessThanEqualAndToDateGreaterThanEqual(user.getUserId(),
                makeLeaveRequest.getFromDate(), makeLeaveRequest.getToDate())).thenReturn(true);

        LeaveOverlappingException exception = assertThrows(LeaveOverlappingException.class,
                () -> leaveRequestServiceImpl.applyForLeave(makeLeaveRequest, "student1"));

        assertEquals("Leave overlapped! You have already applied for the leave"
                + " from " + makeLeaveRequest.getFromDate() + " to " + makeLeaveRequest.getToDate(),
                exception.getMessage());
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(leaveRequestRepository, times(1))
                .existsByUser_UserIdAndFromDateLessThanEqualAndToDateGreaterThanEqual(anyLong(), any(LocalDate.class), any(LocalDate.class));
        verify(teacherRepository, never()).findById(anyLong());
        verify(leaveRequestRepository, never()).save(any(LeaveRequest.class));
        verify(adminRepository, never()).findAll();
    }

    @Test
    void applyForLeave_ResourceNotFoundException_WhenTeacherNotFound(){
        Teacher teacher = Teacher.builder().id(1L).build();

        MakeLeaveRequest makeLeaveRequest = MakeLeaveRequest.builder()
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .approvedByTeacher(teacher)
                .reason("Suffering from typhoid")
                .build();

        User user = User.builder().userId(1L).username("student1").build();

        when(userRepository.findByUsername("student1")).thenReturn(Optional.of(user));

        when(leaveRequestRepository.existsByUser_UserIdAndFromDateLessThanEqualAndToDateGreaterThanEqual(user.getUserId(),
                makeLeaveRequest.getFromDate(), makeLeaveRequest.getToDate())).thenReturn(false);

        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> leaveRequestServiceImpl.applyForLeave(makeLeaveRequest, "student1"));

        assertEquals("Teacher with id: " + makeLeaveRequest.getApprovedByTeacher().getId() +
                        " not found in database.", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(anyString());
        verify(leaveRequestRepository, times(1))
                .existsByUser_UserIdAndFromDateLessThanEqualAndToDateGreaterThanEqual(anyLong(), any(LocalDate.class), any(LocalDate.class));
        verify(teacherRepository, times(1)).findById(anyLong());
        verify(leaveRequestRepository, never()).save(any(LeaveRequest.class));
        verify(adminRepository, never()).findAll();
    }

    @Test
    void viewLeaveApprovalStatus() {

        User user = User.builder().userId(1L).username("student1").build();

        when(userRepository.findByUsername("student1")).thenReturn(Optional.of(user));

        Teacher teacher = Teacher.builder().id(1L).build();

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .leaveId(1L)
                .user(user)
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .approvedByTeacher(teacher)
                .reason("Suffering from typhoid")
                .appliedOn(LocalDate.now())
                .status(LeaveRequestStatus.Pending)
                .build();

        List<LeaveRequest> leaveRequests = List.of(leaveRequest);

        when(leaveRequestRepository.findAllByUser_UserIdAndStatus(user.getUserId(), LeaveRequestStatus.Pending))
                .thenReturn(leaveRequests);

        List<LeaveRequestDto> leaveRequestDtos = leaveRequestServiceImpl.viewLeaveApprovalStatus("student1");

        assertNotNull(leaveRequestDtos);
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(leaveRequestRepository, times(1))
                .findAllByUser_UserIdAndStatus(anyLong(), any(LeaveRequestStatus.class));
    }

    @Test
    void viewLeaveApprovalStatus_ResourceNotFoundException_WhenUserNotFound(){

        when(userRepository.findByUsername("student1")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> leaveRequestServiceImpl.viewLeaveApprovalStatus("student1"));

        assertEquals("User with " + "username: " + "student1" + " not found in database.",
                exception.getMessage());

        verify(userRepository, times(1)).findByUsername(anyString());
        verify(leaveRequestRepository, never())
                .findAllByUser_UserIdAndStatus(anyLong(), any(LeaveRequestStatus.class));
    }
    @Test
    void approveStudentLeaveRequest_approvedByAdmin() {

        when(teacherRepository.findByUser_Username("admin@school.com")).thenReturn(Optional.empty());

        Admin admin = Admin.builder().adminId(1L).username("admin@school.com").build();

        when(adminRepository.findAll()).thenReturn(List.of(admin));

        User user = User.builder().userId(1L).username("student1").build();
        Teacher teacher = Teacher.builder().id(1L).build();

        LeaveRequest existingLeaveRequest = LeaveRequest.builder()
                .leaveId(1L)
                .user(user)
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .approvedByTeacher(null)
                .reason("Suffering from typhoid")
                .appliedOn(LocalDate.now())
                .status(LeaveRequestStatus.Pending)
                .build();

        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(existingLeaveRequest));

        LeaveRequest updatedExistingLeaveRequest = LeaveRequest.builder()
                .leaveId(1L)
                .user(user)
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .approvedByAdmin(admin)
                .reason("Suffering from typhoid")
                .appliedOn(LocalDate.now())
                .status(LeaveRequestStatus.Approved)
                .approvalDate(LocalDate.now())
                .build();

        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Student student = Student.builder().studentRegId(1L).user(user).build();

        when(studentRepository.findByUser_UserId(user.getUserId())).thenReturn(Optional.of(student));

        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(leaveAttendanceSyncRepository.save(any(LeaveAttendanceSync.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LeaveRequestDto result = leaveRequestServiceImpl.approveStudentLeaveRequest("admin@school.com", 1L);

        assertNotNull(result);
        verify(teacherRepository, times(1)).findByUser_Username(anyString());
        verify(adminRepository, times(1)).findAll();
        verify(leaveRequestRepository, times(1)).findById(anyLong());
        verify(leaveRequestRepository, times(1)).save(any(LeaveRequest.class));
        verify(publisher, times(1)).publishEvent(any(LeaveApprovalEvent.class));
        long days = ChronoUnit.DAYS.between(updatedExistingLeaveRequest.getFromDate(),
                updatedExistingLeaveRequest.getToDate()) + 1;
        verify(attendanceRepository, times((int)days)).save(any(Attendance.class));
        verify(leaveAttendanceSyncRepository, times((int)days)).save(any(LeaveAttendanceSync.class));
    }

    @Test
    void approveStudentLeaveRequest_approvedByTeacher(){

        ClassTeacher classTeacher = ClassTeacher.builder().classTeacherId(1L).schoolClass(SchoolClass.builder().classId(1L).build())
                .section(Section.builder().sectionId(1L).build()).build();

        Teacher teacher = Teacher.builder().id(1L).assignedAsClassTeacher(true).classTeacher(classTeacher).build();

        when(teacherRepository.findByUser_Username("teacher1")).thenReturn(Optional.of(teacher));

        User user = User.builder().userId(1L).username("student1").build();

        LeaveRequest existingLeaveRequest = LeaveRequest.builder()
                .leaveId(1L)
                .user(user)
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .approvedByTeacher(teacher)
                .reason("Suffering from typhoid")
                .appliedOn(LocalDate.now())
                .status(LeaveRequestStatus.Pending)
                .build();

        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(existingLeaveRequest));

        LeaveRequest updatedExistingLeaveRequest = LeaveRequest.builder()
                .leaveId(1L)
                .user(user)
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .approvedByTeacher(teacher)
                .reason("Suffering from typhoid")
                .appliedOn(LocalDate.now())
                .status(LeaveRequestStatus.Approved)
                .approvalDate(LocalDate.now())
                .build();

        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Student student = Student.builder().studentRegId(1L).user(user).build();

        when(studentRepository.findByUser_UserId(user.getUserId())).thenReturn(Optional.of(student));

        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(leaveAttendanceSyncRepository.save(any(LeaveAttendanceSync.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LeaveRequestDto result = leaveRequestServiceImpl.approveStudentLeaveRequest("teacher1", 1L);

        assertNotNull(result);
        verify(teacherRepository, times(1)).findByUser_Username(anyString());
        verify(leaveRequestRepository, times(1)).findById(anyLong());
        verify(leaveRequestRepository, times(1)).save(any(LeaveRequest.class));
        verify(publisher, times(1)).publishEvent(any(LeaveApprovalEvent.class));
        long days = ChronoUnit.DAYS.between(updatedExistingLeaveRequest.getFromDate(),
                updatedExistingLeaveRequest.getToDate()) + 1;
        verify(attendanceRepository, times((int)days)).save(any(Attendance.class));
        verify(leaveAttendanceSyncRepository, times((int)days)).save(any(LeaveAttendanceSync.class));
    }

    @Test
    void approveStudentLeaveRequest_CustomUnAuthorizedException_WhenTeacherIsNotClassTeacher(){

        Teacher teacher = Teacher.builder().id(1L).assignedAsClassTeacher(false).build();

        when(teacherRepository.findByUser_Username("teacher1")).thenReturn(Optional.of(teacher));

        CustomUnauthorizedException exception = assertThrows(CustomUnauthorizedException.class,
                () -> leaveRequestServiceImpl.approveStudentLeaveRequest("teacher1", 1L));

        assertEquals("Teacher with id: " + teacher.getId() +
                " is not authorized to perform approve leave request", exception.getMessage());

        verify(teacherRepository, times(1)).findByUser_Username(anyString());
        verify(leaveRequestRepository, never()).findById(anyLong());
        verify(leaveRequestRepository, never()).save(any(LeaveRequest.class));
        verify(publisher, never()).publishEvent(any(LeaveApprovalEvent.class));
        verify(attendanceRepository, never()).save(any(Attendance.class));
        verify(leaveAttendanceSyncRepository, never()).save(any(LeaveAttendanceSync.class));
    }

    @Test
    void rejectStudentLeaveRequest_ByAdmin() {

        when(teacherRepository.findByUser_Username("admin@school.com")).thenReturn(Optional.empty());

        Admin admin = Admin.builder().adminId(1L).username("admin@school.com").build();

        when(adminRepository.findAll()).thenReturn(List.of(admin));

        User user = User.builder().userId(1L).username("student1").build();
        Teacher teacher = Teacher.builder().id(1L).build();

        LeaveRequest existingLeaveRequest = LeaveRequest.builder()
                .leaveId(1L)
                .user(user)
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .approvedByTeacher(null)
                .reason("Suffering from typhoid")
                .appliedOn(LocalDate.now())
                .status(LeaveRequestStatus.Pending)
                .build();

        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(existingLeaveRequest));

        String remarks = "Not get required document that proves you are suffering from typhoid. So rejecting the request";

        LeaveRequest updatedExistingLeaveRequest = LeaveRequest.builder()
                .leaveId(1L)
                .user(user)
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .approvedByAdmin(admin)
                .reason("Suffering from typhoid")
                .appliedOn(LocalDate.now())
                .status(LeaveRequestStatus.Rejected)
                .remarks(remarks)
                .build();

        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LeaveRequestDto result = leaveRequestServiceImpl.rejectStudentLeaveRequest("admin@school.com", 1L, remarks);

        assertNotNull(result);

        verify(teacherRepository, times(1)).findByUser_Username(anyString());
        verify(adminRepository, times(1)).findAll();
        verify(leaveRequestRepository, times(1)).findById(anyLong());
        verify(leaveRequestRepository, times(1)).save(any(LeaveRequest.class));
        verify(publisher, times(1)).publishEvent(any(LeaveApprovalEvent.class));
    }

    @Test
    void rejectStudentLeaveRequest_ByTeacher(){

        ClassTeacher classTeacher = ClassTeacher.builder().classTeacherId(1L).schoolClass(SchoolClass.builder().classId(1L).build())
                .section(Section.builder().sectionId(1L).build()).build();

        Teacher teacher = Teacher.builder().id(1L).assignedAsClassTeacher(true).classTeacher(classTeacher).build();

        when(teacherRepository.findByUser_Username("teacher1")).thenReturn(Optional.of(teacher));

        User user = User.builder().userId(1L).username("student1").build();

        LeaveRequest existingLeaveRequest = LeaveRequest.builder()
                .leaveId(1L)
                .user(user)
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .approvedByTeacher(teacher)
                .reason("Suffering from typhoid")
                .appliedOn(LocalDate.now())
                .status(LeaveRequestStatus.Pending)
                .build();

        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(existingLeaveRequest));

        String remarks = "Not get required document that proves you are suffering from typhoid. So rejecting the request";

        LeaveRequest updatedExistingLeaveRequest = LeaveRequest.builder()
                .leaveId(1L)
                .user(user)
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .approvedByTeacher(teacher)
                .reason("Suffering from typhoid")
                .appliedOn(LocalDate.now())
                .status(LeaveRequestStatus.Rejected)
                .remarks(remarks)
                .build();

        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LeaveRequestDto result = leaveRequestServiceImpl.rejectStudentLeaveRequest("teacher1", 1L, remarks);

        assertNotNull(result);

        verify(teacherRepository, times(1)).findByUser_Username(anyString());
        verify(leaveRequestRepository, times(1)).findById(anyLong());
        verify(leaveRequestRepository, times(1)).save(any(LeaveRequest.class));
        verify(publisher, times(1)).publishEvent(any(LeaveApprovalEvent.class));
    }

    @Test
    void viewAllPendingLeaveRequests() {

        User user = User.builder().userId(1L).username("student1").build();
        Admin admin = Admin.builder().adminId(1L).build();

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .leaveId(1L)
                .user(user)
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .approvedByAdmin(admin)
                .reason("Suffering from typhoid")
                .appliedOn(LocalDate.now())
                .status(LeaveRequestStatus.Pending)
                .build();

        List<LeaveRequest> leaveRequests = List.of(leaveRequest);

        when(leaveRequestRepository.findAllByApprovedByAdmin_Role(Role.ADMIN)).thenReturn(leaveRequests);

        List<LeaveRequestDto> result = leaveRequestServiceImpl.viewAllPendingLeaveRequests();

        assertNotNull(result);
        verify(leaveRequestRepository, times(1)).findAllByApprovedByAdmin_Role(any(Role.class));
    }

    @Test
    void approveTeacherLeaveRequest() {

        User user = User.builder().userId(1L).username("student1").build();
        Admin admin = Admin.builder().adminId(1L).build();

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .leaveId(1L)
                .user(user)
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .approvedByAdmin(admin)
                .reason("Suffering from typhoid")
                .appliedOn(LocalDate.now())
                .status(LeaveRequestStatus.Pending)
                .build();

        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));

        LeaveRequest updatedLeaveRequest = LeaveRequest.builder()
                .leaveId(1L)
                .user(user)
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .approvedByAdmin(admin)
                .reason("Suffering from typhoid")
                .appliedOn(LocalDate.now())
                .status(LeaveRequestStatus.Approved)
                .approvalDate(LocalDate.now())
                .build();

        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Teacher teacher = Teacher.builder().id(1L).build();

        when(teacherRepository.findByUser_UserId(updatedLeaveRequest.getUser().getUserId())).thenReturn(teacher);

        LeaveRequestDto result = leaveRequestServiceImpl.approveTeacherLeaveRequest(1L);

        assertNotNull(result);

        verify(leaveRequestRepository, times(1)).findById(anyLong());
        verify(leaveRequestRepository, times(1)).save(any(LeaveRequest.class));
        verify(teacherRepository, times(1)).findByUser_UserId(anyLong());
    }

    @Test
    void rejectTeacherLeaveRequest() {

        User user = User.builder().userId(1L).username("student1").build();
        Admin admin = Admin.builder().adminId(1L).build();

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .leaveId(1L)
                .user(user)
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .approvedByAdmin(admin)
                .reason("Suffering from typhoid")
                .appliedOn(LocalDate.now())
                .status(LeaveRequestStatus.Pending)
                .build();

        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));

        String remarks = "Not get required document that proves you are suffering from typhoid. So rejecting the request";

        LeaveRequest updatedLeaveRequest = LeaveRequest.builder()
                .leaveId(1L)
                .user(user)
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .approvedByAdmin(admin)
                .reason("Suffering from typhoid")
                .appliedOn(LocalDate.now())
                .status(LeaveRequestStatus.Rejected)
                .remarks(remarks)
                .build();

        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LeaveRequestDto result = leaveRequestServiceImpl.rejectTeacherLeaveRequest(1L, remarks);

        assertNotNull(result);

        verify(leaveRequestRepository, times(1)).findById(anyLong());
        verify(leaveRequestRepository, times(1)).save(any(LeaveRequest.class));
        verify(publisher, times(1)).publishEvent(any(LeaveApprovalEvent.class));
    }

    @Test
    void viewStudentPendingLeaveRequests_Admin() {

        when(teacherRepository.findByUser_Username("admin@school.com")).thenReturn(Optional.empty());

        Admin admin = Admin.builder().adminId(1L).build();

        User user = User.builder().userId(1L).username("student1").build();

        Teacher teacher = Teacher.builder().id(1L).build();

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .leaveId(1L)
                .user(user)
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .approvedByTeacher(teacher)
                .reason("Suffering from typhoid")
                .appliedOn(LocalDate.now())
                .status(LeaveRequestStatus.Pending)
                .build();

        List<LeaveRequest> leaveRequestDtos = List.of(leaveRequest);

        when(leaveRequestRepository.findAllByStatus(LeaveRequestStatus.Pending)).thenReturn(leaveRequestDtos);

        List<LeaveRequestDto> result = leaveRequestServiceImpl.viewStudentPendingLeaveRequests("admin@school.com");

        assertNotNull(result);

        verify(teacherRepository, times(1)).findByUser_Username(anyString());
        verify(leaveRequestRepository, times(1)).findAllByStatus(any(LeaveRequestStatus.class));
    }
}
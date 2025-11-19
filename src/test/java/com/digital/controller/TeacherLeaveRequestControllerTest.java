package com.digital.controller;

import com.digital.dto.LeaveRequestDto;
import com.digital.dto.MakeLeaveRequest;
import com.digital.enums.LeaveRequestStatus;
import com.digital.enums.LeaveType;
import com.digital.securityConfig.AppConfig;
import com.digital.securityConfig.CustomUserDetailsService;
import com.digital.securityConfig.JwtService;
import com.digital.servicei.LeaveRequestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeacherLeaveRequestController.class)
@Import(AppConfig.class)
class TeacherLeaveRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LeaveRequestService leaveRequestService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username="teacher1", roles = {"TEACHER"})
    void applyForLeave() throws Exception {

        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(4);

        String requestBody = String.format("""
                {
                    "leaveType": "Casual",
                    "fromDate": "%s",
                    "toDate": "%s",
                    "reason": "Need to attend brother's marriage ceremony. My presence is significant to family."
                }""", fromDate, toDate);

        LeaveRequestDto leaveRequestDto1 = LeaveRequestDto.builder()
                .appliedOn(LocalDate.now())
                .leaveId(1L)
                .classTeacherName("Anil")
                .status(LeaveRequestStatus.Pending)
                .leaveType(LeaveType.Casual)
                .fromDate(fromDate)
                .toDate(toDate)
                .reason("Brothers Wedding")
                .build();

        when(leaveRequestService.applyForLeave(any(MakeLeaveRequest.class), eq("teacher1")))
                .thenReturn(leaveRequestDto1);

        String expectedJson = objectMapper.writeValueAsString(leaveRequestDto1);

        mockMvc.perform(post("/teacher/leaves/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @WithMockUser(username="teacher1", roles = {"TEACHER", "ADMIN"})
    void viewStudentPendingLeaveRequests() throws Exception {

        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(4);

        LeaveRequestDto leaveRequestDto1 = LeaveRequestDto.builder()
                .appliedOn(LocalDate.now())
                .leaveId(1L)
                .classTeacherName("Anil")
                .status(LeaveRequestStatus.Pending)
                .approvedByTeacher(1L)
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .reason("Suffering from Typhoid. Doctor suggest me to rest for at least 3 days")
                .build();

        LeaveRequestDto leaveRequestDto2 = LeaveRequestDto.builder()
                .appliedOn(LocalDate.now())
                .leaveId(2L)
                .classTeacherName("Anil")
                .status(LeaveRequestStatus.Pending)
                .approvedByTeacher(1L)
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .reason("Suffering from Typhoid. Doctor suggest me to rest for at least 3 days")
                .build();

        List<LeaveRequestDto> leaveRequestDtos = List.of(leaveRequestDto1, leaveRequestDto2);

        String expectedJson = objectMapper.writeValueAsString(leaveRequestDtos);

        when(leaveRequestService.viewStudentPendingLeaveRequests(eq("teacher1")))
                .thenReturn(leaveRequestDtos);

        mockMvc.perform(get("/teacher/student-leave/view")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @WithMockUser(username="teacher1", roles = {"TEACHER", "ADMIN"})
    void approveStudentLeaveRequest() throws Exception {

        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(4);

        LeaveRequestDto leaveRequestDto1 = LeaveRequestDto.builder()
                .appliedOn(LocalDate.now())
                .leaveId(1L)
                .classTeacherName("Anil")
                .status(LeaveRequestStatus.Approved)
                .approvedByTeacher(1L)
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .reason("Suffering from Typhoid. Doctor suggest me to rest for at least 3 days")
                .approvalDate(LocalDate.now())
                .build();

        when(leaveRequestService.approveStudentLeaveRequest(eq("teacher1"), eq(1L))).thenReturn(leaveRequestDto1);

        String expectedJson = objectMapper.writeValueAsString(leaveRequestDto1);

        mockMvc.perform(put("/teacher/student-leave/1/approve")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @WithMockUser(username="teacher1", roles = {"TEACHER", "ADMIN"})
    void rejectStudentLeaveRequest() throws Exception {

        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(4);

        LeaveRequestDto leaveRequestDto1 = LeaveRequestDto.builder()
                .appliedOn(LocalDate.now())
                .leaveId(1L)
                .classTeacherName("Anil")
                .status(LeaveRequestStatus.Rejected)
                .approvedByTeacher(1L)
                .leaveType(LeaveType.Sick)
                .fromDate(fromDate)
                .toDate(toDate)
                .reason("Suffering from Typhoid. Doctor suggest me to rest for at least 3 days")
                .build();

        when(leaveRequestService.rejectStudentLeaveRequest(eq("teacher1"), eq(1L),
                eq("Failing to submit the required medical documents so rejecting the leave request.")))
                .thenReturn(leaveRequestDto1);

        String expectedJson = objectMapper.writeValueAsString(leaveRequestDto1);

        mockMvc.perform(put("/teacher/student-leave/1/reject")
                .param("remarks", "Failing to submit the required medical documents so rejecting the leave request.")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void testUnauthorizedAccess() throws Exception {

        mockMvc.perform(get("/teacher/student-leave/view"))
                .andExpect(status().isUnauthorized());
    }
}
package com.digital.controller;

import com.digital.dto.LeaveRequestDto;
import com.digital.dto.MakeLeaveRequest;
import com.digital.entity.Teacher;
import com.digital.enums.LeaveRequestStatus;
import com.digital.enums.LeaveType;
import com.digital.securityConfig.AppConfig;
import com.digital.securityConfig.CustomUserDetailsService;
import com.digital.securityConfig.JwtService;
import com.digital.servicei.LeaveRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentLeaveRequestController.class)
@Import(AppConfig.class)
class StudentLeaveRequestControllerTest {

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
    @WithMockUser(username = "student1", roles = "STUDENT")
    void applyForLeave() throws Exception {

        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(4);

        String requestBody = String.format("""
                {
                    "leaveType": "Casual",
                    "fromDate": "%s",
                    "toDate": "%s",
                    "reason": "Need to attend brother's marriage ceremony. My presence is significant to family.",
                    "approvedByTeacher": {
                        "id": 1
                    },
                    "approvedByAdmin": {}
                }""", fromDate, toDate);

        Teacher teacher = Teacher.builder().id(1L).firstName("Anil").build();

        LeaveRequestDto leaveRequestDto = LeaveRequestDto.builder()
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

        String expectedJson = objectMapper.writeValueAsString(leaveRequestDto);

        when(leaveRequestService.applyForLeave(any(MakeLeaveRequest.class), eq("student1")))
                .thenReturn(leaveRequestDto);

        mockMvc.perform(post("/student/leaves/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @WithMockUser(username="student1", roles = "STUDENT")
    void viewLeaveApprovalStatus() throws Exception {

        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(4);

        Teacher teacher = Teacher.builder().id(1L).firstName("Anil").build();

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

        when(leaveRequestService.viewLeaveApprovalStatus(eq("student1"))).thenReturn(leaveRequestDtos);

        mockMvc.perform(get("/student/leaves/status")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void testUnauthorizedAccess() throws Exception {

        mockMvc.perform(get("/student/leaves/status"))
                .andExpect(status().isUnauthorized());
    }
}
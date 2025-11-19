package com.digital.controller;

import com.digital.dto.LeaveRequestDto;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AdminLeaveRequestController.class)
@Import(AppConfig.class)
class AdminLeaveRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private LeaveRequestService leaveRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void viewAllPendingLeaveRequests() throws Exception {

        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(4);

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

        LeaveRequestDto leaveRequestDto2 = LeaveRequestDto.builder()
                .appliedOn(LocalDate.now())
                .leaveId(2L)
                .classTeacherName("Anil")
                .status(LeaveRequestStatus.Pending)
                .leaveType(LeaveType.Casual)
                .fromDate(fromDate)
                .toDate(toDate)
                .reason("Brothers Wedding")
                .build();

        List<LeaveRequestDto> leaveRequestDtos = List.of(leaveRequestDto1, leaveRequestDto2);

        when(leaveRequestService.viewAllPendingLeaveRequests()).thenReturn(leaveRequestDtos);

        String expectedJson = objectMapper.writeValueAsString(leaveRequestDtos);

        mockMvc.perform(get("/admin/leave-requests")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void approveTeacherLeaveRequest() throws Exception {

        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(4);

        LeaveRequestDto leaveRequestDto1 = LeaveRequestDto.builder()
                .appliedOn(LocalDate.now())
                .leaveId(1L)
                .classTeacherName("Anil")
                .status(LeaveRequestStatus.Approved)
                .leaveType(LeaveType.Casual)
                .fromDate(fromDate)
                .toDate(toDate)
                .reason("Brothers Wedding")
                .approvalDate(LocalDate.now())
                .build();

        when(leaveRequestService.approveTeacherLeaveRequest(1L)).thenReturn(leaveRequestDto1);

        String expectedJson = objectMapper.writeValueAsString(leaveRequestDto1);

        mockMvc.perform(put("/admin/leave-requests/1/approve")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void rejectTeacherLeaveRequest() throws Exception {

        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(4);

        LeaveRequestDto leaveRequestDto1 = LeaveRequestDto.builder()
                .appliedOn(LocalDate.now())
                .leaveId(1L)
                .classTeacherName("Anil")
                .status(LeaveRequestStatus.Rejected)
                .leaveType(LeaveType.Casual)
                .fromDate(fromDate)
                .toDate(toDate)
                .reason("Brothers Wedding")
                .remarks("Due to inspection in school I am rejecting your request.")
                .build();

        when(leaveRequestService.rejectTeacherLeaveRequest(1L,
                "Due to inspection in school I am rejecting your request."))
                .thenReturn(leaveRequestDto1);

        String expectedJson = objectMapper.writeValueAsString(leaveRequestDto1);

        mockMvc.perform(put("/admin/leave-requests/1/reject")
                        .param("remarks", "Due to inspection in school I am rejecting your request.")
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().json(expectedJson));
    }

    @Test
    void testUnauthorizedAccess() throws Exception {

        mockMvc.perform(get("/admin/leave-requests"))
                .andExpect(status().isUnauthorized());
    }
}
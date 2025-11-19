package com.digital.controller;

import com.digital.dto.TimetableRequest;
import com.digital.dto.UpdateTimetableRequest;
import com.digital.entity.Timetable;
import com.digital.securityConfig.AppConfig;
import com.digital.securityConfig.CustomUserDetailsService;
import com.digital.securityConfig.JwtService;
import com.digital.servicei.TimetableService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TimetableController.class)
@Import(AppConfig.class)
class TimetableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TimetableService timetableService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateTimetable() throws Exception {
        when(timetableService.createTimetable(any(TimetableRequest.class)))
                .thenReturn("Timetable created successfully");

        LocalDate futureDate = LocalDate.now().plusDays(1);

        String requestBody = String.format("""
                {
                    "schoolClass": {"classId": 1},
                    "section": {"sectionId": 2},
                    "subject": {"subjectId": 3},
                    "teacher": {"id": 2},
                    "dayOfWeek": "FRIDAY",
                    "startTime": "%sT11:00:00",
                    "endTime": "%sT12:00:00",
                    "date": "%s",
                    "topic": "Integration",
                    "description": "Introduction to integration and its importance in mathematics"
                }
                """, futureDate, futureDate, futureDate);

        mockMvc.perform(post("/api/admin/timetable")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().string("Timetable created successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateTimetable() throws Exception {
        when(timetableService.updateTimetable(eq(1L), any(UpdateTimetableRequest.class)))
                .thenReturn("Timetable updated successfully");

        LocalDate futureDate = LocalDate.now().plusDays(1);

        String requestBody = String.format("""
                {
                    "teacher": {"id": 1},
                    "subject": {"subjectId": 2},
                    "topic": "Differentiation",
                    "description": "Intro to Differentiation and its importance",
                    "startTime": "%sT13:00:00",
                    "endTime": "%sT14:00:00",
                    "date": "%s"
                }
                """, futureDate, futureDate, futureDate);

        mockMvc.perform(put("/api/admin/timetable/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Timetable updated successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetTimetables() throws Exception {
        List<Timetable> timetables = Arrays.asList(new Timetable(), new Timetable());
        when(timetableService.getTimetables()).thenReturn(timetables);

        mockMvc.perform(get("/api/admin/timetable")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteTimetable() throws Exception {
        when(timetableService.deleteTimetable(1L)).thenReturn("Deleted successfully");

        mockMvc.perform(delete("/api/admin/timetable/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted successfully"));
    }

    @Test
    void testUnauthorizedAccess() throws Exception {
        // No @WithMockUser → should fail with 401
        mockMvc.perform(get("/api/admin/timetable"))
                .andExpect(status().isUnauthorized());
    }
}

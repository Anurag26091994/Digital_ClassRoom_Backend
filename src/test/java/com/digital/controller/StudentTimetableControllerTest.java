package com.digital.controller;

import com.digital.entity.Session;
import com.digital.entity.Timetable;
import com.digital.securityConfig.AppConfig;
import com.digital.securityConfig.CustomUserDetailsService;
import com.digital.securityConfig.JwtService;
import com.digital.servicei.SessionService;
import com.digital.servicei.TimetableService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentTimetableController.class)
@Import(AppConfig.class) // import your security config if needed
class StudentTimetableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private TimetableService timetableService;

    @MockitoBean
    private SessionService sessionService;

    @Test
    @WithMockUser(roles = "STUDENT")
    void testGetStudentTimetableBySectionId() throws Exception {
        List<Timetable> timetables = Arrays.asList(new Timetable(), new Timetable());
        Mockito.when(timetableService.getStudentTimetableBySectionId(anyLong())).thenReturn(timetables);

        mockMvc.perform(get("/student/timetable/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testGetStudentSessionsBySectionId() throws Exception {
        List<Session> sessions = Arrays.asList(new Session(), new Session());
        Mockito.when(sessionService.getStudentSessions(anyLong())).thenReturn(sessions);

        mockMvc.perform(get("/student/sessions/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testUnauthorizedAccess() throws Exception {
        // No @WithMockUser → should return 401
        mockMvc.perform(get("/student/timetable/1"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/student/sessions/1"))
                .andExpect(status().isUnauthorized());
    }
}

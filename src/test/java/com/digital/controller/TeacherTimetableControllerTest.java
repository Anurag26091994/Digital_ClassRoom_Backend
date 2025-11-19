package com.digital.controller;

import com.digital.entity.Session;
import com.digital.entity.Timetable;
import com.digital.securityConfig.AppConfig;
import com.digital.securityConfig.CustomUserDetailsService;
import com.digital.securityConfig.JwtService;
import com.digital.servicei.SessionService;
import com.digital.servicei.TeacherTimetableService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeacherTimetableController.class)
@Import(AppConfig.class)
class TeacherTimetableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private TeacherTimetableService teacherTimetableService;

    @MockitoBean
    private SessionService sessionService;

    @Test
    @WithMockUser(roles = "TEACHER")
    void testGetTimetableByTeacherId() throws Exception {
        Timetable t1 = new Timetable();
        Timetable t2 = new Timetable();
        List<Timetable> timetables = Arrays.asList(t1, t2);

        when(teacherTimetableService.getTimetableByTeacherId(1L)).thenReturn(timetables);

        mockMvc.perform(get("/teacher/timetable/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void testGetSessionsByTeacherId() throws Exception {
        Session s1 = new Session();
        s1.setStartTime(LocalDateTime.now().plusDays(1));
        Session s2 = new Session();
        s2.setStartTime(LocalDateTime.now().plusDays(1));
        List<Session> sessions = Arrays.asList(s1, s2);

        when(sessionService.getTeacherSessions(1L)).thenReturn(sessions);

        mockMvc.perform(get("/teacher/sessions/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testUnauthorizedAccess() throws Exception {
        // No @WithMockUser → should fail with 401
        mockMvc.perform(get("/teacher/timetable/1"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/teacher/sessions/1"))
                .andExpect(status().isUnauthorized());
    }
}

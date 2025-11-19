package com.digital.controller;

import com.digital.dto.EventDto;
import com.digital.enums.EventType;
import com.digital.securityConfig.AppConfig;
import com.digital.securityConfig.CustomUserDetailsService;
import com.digital.securityConfig.JwtService;
import com.digital.servicei.CalendarService;
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

import java.time.LocalDate;
import java.time.Year;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeacherCalendarController.class)
@Import(AppConfig.class)
class TeacherCalendarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CalendarService calendarService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "TEACHER")
    void viewCalenderEvents() throws Exception {

        LocalDate futureDate = LocalDate.now().plusDays(1);

        Year currentYear = Year.now();
        Year nextYear = Year.now().plusYears(1);

        EventDto eventDto = EventDto.builder()
                .eventId(1L)
                .calenderId(1L)
                .academicYear(currentYear + "-" + nextYear)
                .eventType(EventType.Workshop)
                .eventName("Soft skills and Personality development")
                .eventDate(futureDate)
                .schoolClasses(Arrays.asList("1", "2"))
                .sections(Arrays.asList("1", "2", "3", "4"))
                .description("Learning soft skills and Personality development that are essential for cracking interviews")
                .build();

        when(calendarService.viewCalenderEvents())
                .thenReturn(Arrays.asList(eventDto, eventDto));

        String expectedJson = objectMapper.writeValueAsString(Arrays.asList(eventDto, eventDto));

        mockMvc.perform(get("/teacher/calendar")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void testUnauthorizedAccess() throws Exception {

        mockMvc.perform(get("/teacher/calendar"))
                .andExpect(status().isUnauthorized());
    }
}
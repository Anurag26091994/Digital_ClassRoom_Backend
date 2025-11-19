package com.digital.controller;

import com.digital.dto.*;
import com.digital.entity.Event;
import com.digital.entity.Holiday;
import com.digital.enums.EventType;
import com.digital.enums.Role;
import com.digital.securityConfig.AppConfig;
import com.digital.securityConfig.CustomUserDetailsService;
import com.digital.securityConfig.JwtService;
import com.digital.servicei.CalendarService;
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
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminCalendarController.class)
@Import(AppConfig.class)
class AdminCalendarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CalendarService calendarService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin@school.com", roles = "ADMIN")
    void createAcademicCalendar() throws Exception {

        LocalDate futureDate = LocalDate.now().plusDays(1);

        Year currentYear = Year.now();
        Year nextYear = Year.now().plusYears(1);

        String requestBody = String.format("""
                {
                     "academicYear": "%s-%s",
                     "startDate": "%s",
                     "endDate": "%s"
                }
        """, currentYear, nextYear, futureDate, futureDate.plusDays(364));

        CalendarDto calendarDto = CalendarDto.builder()
                .calendarId(1L)
                .academicYear(currentYear + "-" + nextYear)
                .startDate(futureDate)
                .endDate(futureDate.plusDays(364))
                .createdBy(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build();

        when(calendarService.createAcademicCalender(any(CreateAcademicCalenderRequest.class), eq("admin@school.com")))
                .thenReturn(calendarDto);

        String expectedJson = objectMapper.writeValueAsString(calendarDto);

        mockMvc.perform(post("/admin/calendar")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAcademicCalendars() throws Exception {

        LocalDate futureDate = LocalDate.now().plusDays(1);

        Year currentYear = Year.now();
        Year nextYear = Year.now().plusYears(1);

        CalendarDto calendarDto1 = CalendarDto.builder()
                .calendarId(1L)
                .academicYear(currentYear + "-" + nextYear)
                .startDate(futureDate)
                .endDate(futureDate.plusDays(364))
                .createdBy(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build();

        CalendarDto calendarDto2 = CalendarDto.builder()
                .calendarId(2L)
                .academicYear(currentYear + "-" + nextYear)
                .startDate(futureDate)
                .endDate(futureDate.plusDays(364))
                .createdBy(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build();

        List<CalendarDto> list = Arrays.asList(calendarDto1, calendarDto2);

        when(calendarService.getAcademicCalenders()).thenReturn(list);

        String expectedJson = objectMapper.writeValueAsString(list);

        mockMvc.perform(get("/admin/calendar/view-all"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAcademicCalendar() throws Exception {

        LocalDate futureDate = LocalDate.now().plusDays(1);

        Year currentYear = Year.now();
        Year nextYear = Year.now().plusYears(1);

        CalendarDto calendarDto = CalendarDto.builder()
                .calendarId(1L)
                .academicYear(currentYear + "-" + nextYear)
                .startDate(futureDate)
                .endDate(futureDate.plusDays(364))
                .createdBy(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build();

        when(calendarService.getAcademicCalender(1L)).thenReturn(calendarDto);

        String expectedJson = objectMapper.writeValueAsString(calendarDto);

        mockMvc.perform(get("/admin/calendar/view/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateAcademicCalendar() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(1);

        Year currentYear = Year.now();
        Year nextYear = Year.now().plusYears(1);

        LocalDate endDate = futureDate.plusDays(363);

        String requestBody = String.format("""
                {
                    "academicYear": "%s-%s",
                    "startDate": "%s",
                    "endDate": "%s"
                }""", currentYear, nextYear, futureDate, endDate);

        CalendarDto calendarDto = CalendarDto.builder()
                .calendarId(1L)
                .academicYear(currentYear + "-" + nextYear)
                .startDate(futureDate)
                .endDate(futureDate.plusDays(364))
                .createdBy(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(calendarService.updateAcademicCalender(eq(1L), any(UpdateAcademicCalenderRequest.class)))
                .thenReturn(calendarDto);

        String expectedJson = objectMapper.writeValueAsString(calendarDto);

        mockMvc.perform(put("/admin/calendar/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addHoliday() throws Exception {

        LocalDate futureDate = LocalDate.now().plusDays(1);

        Year currentYear = Year.now();
        Year nextYear = Year.now().plusYears(1);

        String requestBody = String.format("""
                {
                    "calendar": {
                        "calendarId": 1
                    },
                    "holidayDate": "%s",
                    "holidayName": "Dussehra, Mahatma Gandhi Jayanti",
                    "isEmergency": false,
                    "rescheduleRequired": false,
                    "rescheduledDate": ""
                }""", futureDate);

        HolidayDto holidayDto = HolidayDto.builder()
                .holidayId(1L)
                .calendarId(1L)
                .academicYear(currentYear + "-" + nextYear)
                .holidayDate(futureDate)
                .holidayName("Dussehra, Mahatma Gandhi Jayanti")
                .isEmergency(false)
                .rescheduleRequired(false)
                .rescheduledDate(null)
                .build();

        when(calendarService.addHoliday(eq(1L), any(Holiday.class)))
                .thenReturn(holidayDto);

        String expectedJson = objectMapper.writeValueAsString(holidayDto);

        mockMvc.perform(post("/admin/calendar/1/holidays")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateHoliday() throws Exception {

        LocalDate futureDate = LocalDate.now().plusDays(1);

        Year currentYear = Year.now();
        Year nextYear = Year.now().plusYears(1);

        String requestBody =  String.format("""
                {
                    "calendar": {
                        "calendarId": 1
                    },
                    "holidayDate": "%s",
                    "holidayName": "Dussehra, Mahatma Gandhi Jayanti",
                    "isEmergency": true,
                    "rescheduleRequired": true,
                    "rescheduledDate": "%s"
                }
                """, futureDate, futureDate);

        HolidayDto holidayDto = HolidayDto.builder()
                .holidayId(1L)
                .calendarId(1L)
                .academicYear(currentYear + "-" + nextYear)
                .holidayDate(futureDate)
                .holidayName("Dussehra, Mahatma Gandhi Jayanti")
                .isEmergency(true)
                .rescheduleRequired(true)
                .rescheduledDate(futureDate)
                .build();

        when(calendarService.updateHoliday(eq(1L), any(Holiday.class)))
                .thenReturn(holidayDto);

        String expectedJson = objectMapper.writeValueAsString(holidayDto);

        mockMvc.perform(put("/admin/calendar/holidays/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeHoliday() throws Exception {

        when(calendarService.removeHoliday(eq(1L)))
                .thenReturn("Holiday record deleted successfully");

        mockMvc.perform(delete("/admin/calendar/holidays/remove/1")
                .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Holiday record deleted successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addEvent() throws Exception {

        LocalDate futureDate = LocalDate.now().plusDays(1);

        Year currentYear = Year.now();
        Year nextYear = Year.now().plusYears(1);

        String requestBody = String.format("""
                {
                    "calendar": {
                        "calendarId": 1
                    },
                    "eventType": "Workshop",
                    "eventName": "Soft skills and Personality development",
                    "eventDate": "%s",
                    "schoolClasses": [
                        {
                            "classId": 1
                        },
                        {
                            "classId": 2
                        }
                    ],
                    "sections": [
                        {
                            "sectionId": 1
                        },
                        {
                            "sectionId": 2
                        },
                        {
                            "sectionId": 3
                        },
                        {
                            "sectionId": 4
                        }
                    ],
                    "description": "Learning soft skills and Personality development that are essential for cracking interviews"
                }""", futureDate);

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

        when(calendarService.addEvent(eq(1L), any(Event.class))).thenReturn(eventDto);

        String expectedJson = objectMapper.writeValueAsString(eventDto);

        mockMvc.perform(post("/admin/calendar/1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateEvent() throws Exception {

        LocalDate futureDate = LocalDate.now().plusDays(1);

        Year currentYear = Year.now();
        Year nextYear = Year.now().plusYears(1);

        String requestBody = String.format("""
                {
                    "calendar": {
                        "calendarId": 1
                    },
                    "eventType": "Workshop",
                    "eventName": "Soft skills and Personality development",
                    "eventDate": "%s",
                    "schoolClasses": [
                        {
                            "classId": 1
                        }
                    ],
                    "sections": [
                        {
                            "sectionId": 1
                        },
                        {
                            "sectionId": 2
                        }
                    ],
                    "description": "Learning soft skills and Personality development that are essential for cracking interviews"
                }""", futureDate);

        EventDto eventDto = EventDto.builder()
                .eventId(1L)
                .calenderId(1L)
                .academicYear(currentYear + "-" + nextYear)
                .eventType(EventType.Workshop)
                .eventName("Soft skills and Personality development")
                .eventDate(futureDate)
                .schoolClasses(Arrays.asList("1"))
                .sections(Arrays.asList("1", "2"))
                .description("Learning soft skills and Personality development that are essential for cracking interviews")
                .build();

        when(calendarService.updateEvent(eq(1L), any(Event.class)))
                .thenReturn(eventDto);

        String expectedJson = objectMapper.writeValueAsString(eventDto);

        mockMvc.perform(put("/admin/calendar/events/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeEvent() throws Exception {

        when(calendarService.removeEvent(eq(1L))).thenReturn("Event record deleted successfully");

        mockMvc.perform(delete("/admin/calendar/events/remove/1")
                .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Event record deleted successfully"));
    }

    @Test
    void testUnauthorizedAccess() throws Exception {
        // No @WithMockUser → should fail with 401
        mockMvc.perform(get("/admin/calendar/view-all"))
                .andExpect(status().isUnauthorized());
    }

}
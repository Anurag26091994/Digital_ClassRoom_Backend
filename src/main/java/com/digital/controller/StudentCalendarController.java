package com.digital.controller;

import com.digital.dto.EventDto;
import com.digital.servicei.CalendarService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin("*")
@RestController
@AllArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
@RequestMapping(path = "/student")
public class StudentCalendarController {

    private final CalendarService calendarService;

    /**
     * API to view calendar events for STUDENT
     * */

    @GetMapping(path = "/calendar", produces = "application/json")
    public ResponseEntity<List<EventDto>> viewCalenderEvents(){

        return new ResponseEntity<List<EventDto>>(calendarService.viewCalenderEvents(), HttpStatus.OK);
    }
}

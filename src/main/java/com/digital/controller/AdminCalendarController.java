package com.digital.controller;

import com.digital.dto.*;
import com.digital.entity.Event;
import com.digital.entity.Holiday;
import com.digital.servicei.CalendarService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping(path = "/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCalendarController {

    private final CalendarService calendarService;

    /**
     * API to create academic calendar for ADMIN.
     * */

    @PostMapping(path = "/calendar", consumes = "application/json", produces = "application/json")
    public ResponseEntity<CalendarDto> createAcademicCalendar(
            @Valid @RequestBody CreateAcademicCalenderRequest request){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return new ResponseEntity<CalendarDto>(calendarService.createAcademicCalender(request, username),
                HttpStatus.CREATED);
    }

    /**
     * API to get academic calendar records for ADMIN.
     * */

    @GetMapping(path = "/calendar/view-all", produces = "application/json")
    public ResponseEntity<List<CalendarDto>> getAcademicCalendars(){

        return new ResponseEntity<List<CalendarDto>>(calendarService.getAcademicCalenders(),
                HttpStatus.OK);
    }

    /**
     * API to get specific academic calendar record for ADMIN.
     * */

    @GetMapping(path = "/calendar/view/{calendarId}", produces = "application/json")
    public ResponseEntity<CalendarDto> getAcademicCalendar(@PathVariable Long calendarId){

        return new ResponseEntity<CalendarDto>(calendarService.getAcademicCalender(calendarId),
                HttpStatus.OK);
    }

    /**
     * API to update academic calender record for ADMIN.
     * */

    @PutMapping(path = "/calendar/{calendarId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<CalendarDto> updateAcademicCalendar(
            @PathVariable Long calendarId,
            @Valid @RequestBody UpdateAcademicCalenderRequest request){

        return new ResponseEntity<CalendarDto>(calendarService.updateAcademicCalender(calendarId, request),
                HttpStatus.OK);
    }

    /**
     * API to add holiday using calendar id for ADMIN.
     * ADMIn can add emergency holiday as well whenever required.
     * */

    @PostMapping(path = "/calendar/{calendarId}/holidays", consumes = "application/json", produces = "application/json")
    public ResponseEntity<HolidayDto> addHoliday(@PathVariable Long calendarId, @Valid @RequestBody Holiday holiday){

        return new ResponseEntity<HolidayDto>(calendarService.addHoliday(calendarId, holiday), HttpStatus.CREATED);
    }

    /**
     * ADMIN api to view holiday using holidayId
     * */

    @GetMapping(path = "/calendar/holidays/{holidayId}/view", produces = "application/json")
    public ResponseEntity<HolidayDto> viewHoliday(@PathVariable Long holidayId){

        return new ResponseEntity<HolidayDto>(calendarService.viewHoliday(holidayId), HttpStatus.OK);
    }

    /**
     * ADMIN api to view all holidays within the current academic year.
     * */

    @GetMapping(path = "/calendar/holidays/view-all", produces = "application/json")
    public ResponseEntity<List<HolidayDto>> viewHolidays(){

        return new ResponseEntity<List<HolidayDto>>(calendarService.viewHolidays(), HttpStatus.OK);
    }

    /**
     * API to update holiday details and also used to reschedule the date of holiday for ADMIN.
     * */

    @PutMapping(path = "/calendar/holidays/{holidayId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<HolidayDto> updateHoliday(@PathVariable Long holidayId, @Valid @RequestBody Holiday holiday){

        return new ResponseEntity<HolidayDto>(calendarService.updateHoliday(holidayId, holiday), HttpStatus.OK);
    }

    /**
     * API to remove holiday from calendar for ADMIN
     * */

    @DeleteMapping(path = "/calendar/holidays/remove/{holidayId}")
    public ResponseEntity<String> removeHoliday(@PathVariable Long holidayId){

        return new ResponseEntity<String>(calendarService.removeHoliday(holidayId), HttpStatus.OK);
    }

    /**
     * API to add event using calendar id for ADMIN.
     * */

    @PostMapping(path = "/calendar/{calendarId}/events", consumes = "application/json", produces = "application/json")
    public ResponseEntity<EventDto> addEvent(@PathVariable Long calendarId, @Valid @RequestBody Event event){

        return new ResponseEntity<EventDto>(calendarService.addEvent(calendarId, event), HttpStatus.CREATED);
    }

    /**
     * ADMIN api to view Event using eventId
     * */

    @GetMapping(path = "/calendar/events/{eventId}/view", produces = "application/json")
    public ResponseEntity<EventDto> viewEvent(@PathVariable Long eventId){

        return new ResponseEntity<EventDto>(calendarService.viewEvent(eventId), HttpStatus.OK);
    }

    /**
     * ADMIn api to view all the calendar events.
     * */

    @GetMapping(path = "/calendar/events/view-all", produces = "application/json")
    public ResponseEntity<List<EventDto>> viewCalenderEvents(){

        return new ResponseEntity<List<EventDto>>(calendarService.viewCalenderEvents(), HttpStatus.OK);
    }

    /**
     * API to update event using calendar id for ADMIN.
     * This api is used to reschedule the event.
     * */

    @PutMapping(path = "/calendar/events/{eventId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Long eventId, @Valid @RequestBody Event event){

        return new ResponseEntity<EventDto>(calendarService.updateEvent(eventId, event), HttpStatus.OK);
    }

    /**
     * API to remove holiday from calendar for ADMIN
     * */

    @DeleteMapping(path = "/calendar/events/remove/{eventId}")
    public ResponseEntity<String> removeEvent(@PathVariable Long eventId){

        return new ResponseEntity<String>(calendarService.removeEvent(eventId), HttpStatus.OK);
    }
}

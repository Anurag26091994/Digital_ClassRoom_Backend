package com.digital.servicei;

import com.digital.dto.*;
import com.digital.entity.Event;
import com.digital.entity.Holiday;

import java.util.List;

public interface CalendarService {

    CalendarDto createAcademicCalender(CreateAcademicCalenderRequest request, String username);

    HolidayDto addHoliday(Long calendarId, Holiday holiday);

    EventDto addEvent(Long calendarId, Event event);

    List<EventDto> viewCalenderEvents();

    CalendarDto updateAcademicCalender(Long calendarId, UpdateAcademicCalenderRequest request);

    List<CalendarDto> getAcademicCalenders();

    CalendarDto getAcademicCalender(Long calendarId);

    HolidayDto updateHoliday(Long holidayId, Holiday holiday);

    String removeHoliday(Long holidayId);

    String removeEvent(Long eventId);

    EventDto updateEvent(Long eventId, Event event);

    EventDto viewEvent(Long eventId);

    HolidayDto viewHoliday(Long holidayId);

    List<HolidayDto> viewHolidays();

}

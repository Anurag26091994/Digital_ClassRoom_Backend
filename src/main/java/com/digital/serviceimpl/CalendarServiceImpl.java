package com.digital.serviceimpl;

import com.digital.dto.*;
import com.digital.entity.*;
import com.digital.enums.DayOfWeek;
import com.digital.enums.EventType;
import com.digital.events.EmergencyHolidayEvent;
import com.digital.events.RescheduledEvent;
import com.digital.events.RescheduledHolidayEvent;
import com.digital.exception.DuplicateResourceException;
import com.digital.exception.InvalidDateException;
import com.digital.exception.ResourceNotFoundException;
import com.digital.repository.*;
import com.digital.servicei.CalendarService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@AllArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final AcademicCalendarRepository academicCalendarRepository;

    private final AdminRepository adminRepository;

    private final HolidayRepository holidayRepository;

    private final EventRepository eventRepository;

    private final ClassRepository classRepository;

    private final SectionRepository sectionRepository;

    private final ApplicationEventPublisher publisher;

    private final StudentRepository studentRepository;

    private final TeacherRepository teacherRepository;

    private final TimetableRepository timetableRepository;

    private final SessionRepository sessionRepository;

    @Override
    public CalendarDto createAcademicCalender(CreateAcademicCalenderRequest request, String username) {

        if(academicCalendarRepository.existsByAcademicYear(request.getAcademicYear())){
            throw new DuplicateResourceException("Academic calendar with academic year " + request.getAcademicYear() + " is already present.");
        }

        Admin admin = adminRepository.findByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException("Admin record not found in database"));

        if(request.getStartDate().isAfter(request.getEndDate())){
            throw new InvalidDateException("Start date must be before end date.");
        }

        AcademicCalendar academicCalendar = AcademicCalendar.builder()
                .academicYear(request.getAcademicYear())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .createdBy(admin)
                .build();

        AcademicCalendar savedAcademicCalender = academicCalendarRepository.save(academicCalendar);

        return mapToCalenderDto(savedAcademicCalender);
    }

    @Override
    public CalendarDto updateAcademicCalender(Long calendarId, UpdateAcademicCalenderRequest request) {

        AcademicCalendar academicCalendar = academicCalendarRepository.findById(calendarId).orElseThrow(
                () -> new ResourceNotFoundException("Academic calendar record not found in database."));

        academicCalendar.setAcademicYear(request.getAcademicYear());
        academicCalendar.setStartDate(request.getStartDate());
        academicCalendar.setEndDate(request.getEndDate());

        AcademicCalendar updatedAcademicCalendar = academicCalendarRepository.save(academicCalendar);

        return mapToCalenderDto(updatedAcademicCalendar);
    }

    @Override
    public List<CalendarDto> getAcademicCalenders() {

        return academicCalendarRepository.findAll().stream()
                .map(this::mapToCalenderDto)
                .toList();
    }

    @Override
    public CalendarDto getAcademicCalender(Long calendarId) {

        AcademicCalendar academicCalendar = academicCalendarRepository.findById(calendarId).orElseThrow(
                () -> new ResourceNotFoundException("Academic calendar record not found in database."));

        return mapToCalenderDto(academicCalendar);
    }

    @Override
    public HolidayDto addHoliday(Long calendarId, Holiday holiday) {

        AcademicCalendar academicCalendar = academicCalendarRepository.findById(calendarId).orElseThrow(() ->
                new ResourceNotFoundException("Academic calendar record not found in database"));

        if(holiday.getHolidayDate().isAfter(academicCalendar.getEndDate()) |
                holiday.getHolidayDate().isBefore(academicCalendar.getStartDate())){

            throw new InvalidDateException("Holiday date must be within academic calendar year");
        }
        holiday.setCalendar(academicCalendar);

        Holiday savedHoliday = holidayRepository.save(holiday);

        List<Long> studentIds = studentRepository.findAll().stream().map(Student::getStudentRegId).toList();

        List<Long> teacherIds = teacherRepository.findAll().stream().map(Teacher::getId).toList();

        /* If emergency holiday is declared then all timetables and sessions on emergency holiday date are rescheduled to
           the given timetableRescheduleDate. So it is important to provide timetableRescheduleDate in case of emergency
           holiday.
           In case of session, as session date is changing so google meet link date also get changes so here we are removing
           old google meet links from sessions. Teacher has to add join link explicitly before session day.
        */

        if(savedHoliday.getIsEmergency()) {

            publisher.publishEvent(new EmergencyHolidayEvent(savedHoliday.getHolidayId(),
                    savedHoliday.getHolidayDate(), studentIds, teacherIds));

            LocalDate timetableRescheduleDate = savedHoliday.getTimetableRescheduleDate();

            List<Timetable> timetables = timetableRepository.findAllByDate(savedHoliday.getHolidayDate());

            timetables.forEach(timetable -> {

                /* We are extracting the local time from old start time and end, time which are of type
                   LocalDateTime. Because we should be able to change the date from start time and end time,
                   and time is going to be same.
                 */

                LocalTime startLocalTime = timetable.getStartTime().toLocalTime();
                LocalTime endLocalTime = timetable.getEndTime().toLocalTime();

                timetable.setDate(timetableRescheduleDate);
                timetable.setStartTime(LocalDateTime.of(timetableRescheduleDate, startLocalTime));
                timetable.setEndTime(LocalDateTime.of(timetableRescheduleDate, endLocalTime));

                String dayName = timetableRescheduleDate.getDayOfWeek().name();
                timetable.setDayOfWeek(DayOfWeek.valueOf(dayName));

                timetableRepository.save(timetable);
            });

            List<Session> sessions = sessionRepository.findAllByDate(savedHoliday.getHolidayDate());

            sessions.forEach(session -> {

                LocalTime startLocalTime = session.getStartTime().toLocalTime();
                LocalTime endLocalTime = session.getEndTime().toLocalTime();

                session.setDate(timetableRescheduleDate);
                session.setStartTime(LocalDateTime.of(timetableRescheduleDate, startLocalTime));
                session.setEndTime(LocalDateTime.of(timetableRescheduleDate, endLocalTime));
                session.setJoinLink(null);

                sessionRepository.save(session);
            });

            List<Event> events = eventRepository.findAllByEventTypeAndEventDate(EventType.Exam, savedHoliday.getHolidayDate());

            events.forEach(event -> {

                event.setEventDate(savedHoliday.getEventRescheduleDate());
                Event savedEvent = eventRepository.save(event);

                // publishing exam reschedule through handleRescheduledEvent listener

                publisher.publishEvent(new RescheduledEvent(savedEvent.getEventId(),
                        savedEvent.getEventName(), event.getEventDate(), savedEvent.getEventDate(),
                        studentIds, teacherIds));
            });
        }

        return mapToHolidayDto(savedHoliday);
    }

    @Override
    public HolidayDto updateHoliday(Long holidayId, Holiday holiday) {

        Holiday existingHoliday = holidayRepository.findById(holidayId).orElseThrow(
                () -> new ResourceNotFoundException("Holiday record not found in database."));

        AcademicCalendar academicCalendar = academicCalendarRepository.findById(holiday.getCalendar().getCalendarId())
                .orElseThrow(() -> new ResourceNotFoundException("Academic calendar record not found in database."));

        existingHoliday.setCalendar(academicCalendar);
        existingHoliday.setHolidayName(holiday.getHolidayName());
        existingHoliday.setHolidayDate(holiday.getHolidayDate());
        existingHoliday.setIsEmergency(holiday.getIsEmergency());
        existingHoliday.setRescheduleRequired(holiday.getRescheduleRequired());
        existingHoliday.setRescheduledDate(holiday.getRescheduledDate());

        Holiday savedHoliday = holidayRepository.save(existingHoliday);

        List<Long> studentIds = studentRepository.findAll().stream().map(Student::getStudentRegId).toList();

        List<Long> teacherIds = teacherRepository.findAll().stream().map(Teacher::getId).toList();

        if(savedHoliday.getRescheduleRequired()) {
            publisher.publishEvent(new RescheduledHolidayEvent(savedHoliday.getHolidayId(),
                    savedHoliday.getHolidayName(), savedHoliday.getHolidayDate(),
                    savedHoliday.getRescheduledDate(), studentIds, teacherIds));
        }

        return mapToHolidayDto(savedHoliday);
    }

    @Override
    public String removeHoliday(Long holidayId) {

        Holiday holiday = holidayRepository.findById(holidayId).orElseThrow(
                () -> new ResourceNotFoundException("Holiday record not found in database."));

        holidayRepository.delete(holiday);

        return "Holiday record deleted successfully.";
    }

    @Override
    public HolidayDto viewHoliday(Long holidayId) {

        Holiday holiday = holidayRepository.findById(holidayId).orElseThrow(() ->
                new ResourceNotFoundException("Holiday record with id: " + holidayId + " not found in database"));

        return mapToHolidayDto(holiday);
    }

    @Override
    public List<HolidayDto> viewHolidays() {

        LocalDate currentDate = LocalDate.now();

        return holidayRepository.findAll().stream()
                .filter(holiday -> currentDate.isAfter(holiday.getCalendar().getStartDate()) &
                                   currentDate.isBefore(holiday.getCalendar().getEndDate()))
                .map(this::mapToHolidayDto)
                .toList();
    }

    @Override
    public EventDto addEvent(Long calendarId, Event event) {

        AcademicCalendar academicCalendar = academicCalendarRepository.findById(calendarId).orElseThrow(() ->
                new ResourceNotFoundException("Academic calendar record not found in database"));

        if(event.getEventDate().isAfter(academicCalendar.getEndDate()) |
                event.getEventDate().isBefore(academicCalendar.getStartDate())){

            throw new InvalidDateException("Event date must be within academic year.");
        }

        List<Long> classIds = event.getSchoolClasses().stream().map(SchoolClass::getClassId).toList();
        List<SchoolClass> schoolClasses = classRepository.findAllById(classIds);

        List<Long> sectionIds = event.getSections().stream().map(Section::getSectionId).toList();
        List<Section> sections = sectionRepository.findAllById(sectionIds);

        event.setCalendar(academicCalendar);
        event.setSchoolClasses(schoolClasses);
        event.setSections(sections);

        Event savedEvent = eventRepository.save(event);

        return mapToEventDto(savedEvent);
    }

    @Override
    public List<EventDto> viewCalenderEvents() {

        LocalDate currentDate = LocalDate.now();

        List<Event> events = eventRepository.findAll()
                                .stream()
                                .filter(e -> currentDate.isAfter(e.getCalendar().getStartDate()) &
                                 currentDate.isBefore(e.getCalendar().getEndDate())).toList();

        return events.stream().map(this::mapToEventDto).toList();
    }

    @Override
    public String removeEvent(Long eventId) {

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ResourceNotFoundException("Event record not found in database."));

        eventRepository.delete(event);

        return "Event record deleted successfully.";
    }

    @Override
    public EventDto updateEvent(Long eventId, Event event) {

        Event existingEvent = eventRepository.findById(eventId).orElseThrow(
                () -> new ResourceNotFoundException("Event record not found in database."));

        AcademicCalendar academicCalendar = academicCalendarRepository.findById(event.getCalendar()
                .getCalendarId()).orElseThrow(() ->
                new ResourceNotFoundException("Academic calendar record not found in database"));

        if(event.getEventDate().isAfter(academicCalendar.getEndDate()) |
                event.getEventDate().isBefore(academicCalendar.getStartDate())){

            throw new InvalidDateException("Event date must be within academic year.");
        }

        List<Long> classIds = event.getSchoolClasses().stream().map(SchoolClass::getClassId).toList();
        List<SchoolClass> schoolClasses = classRepository.findAllById(classIds);

        List<Long> sectionIds = event.getSections().stream().map(Section::getSectionId).toList();
        List<Section> sections = sectionRepository.findAllById(sectionIds);

        existingEvent.setEventType(event.getEventType());
        existingEvent.setEventName(event.getEventName());
        existingEvent.setEventDate(event.getEventDate());
        existingEvent.setDescription(event.getDescription());
        existingEvent.setCalendar(academicCalendar);
        existingEvent.setSchoolClasses(schoolClasses);
        existingEvent.setSections(sections);

        Event savedEvent = eventRepository.save(existingEvent);

        List<Long> studentIds = studentRepository.findAll().stream().map(Student::getStudentRegId).toList();

        List<Long> teacherIds = teacherRepository.findAll().stream().map(Teacher::getId).toList();

        if(!event.getEventDate().equals(existingEvent.getEventDate())){

            publisher.publishEvent(new RescheduledEvent(savedEvent.getEventId(), savedEvent.getEventName(),
                    existingEvent.getEventDate(), savedEvent.getEventDate(), studentIds, teacherIds));
        }

        return mapToEventDto(savedEvent);
    }

    @Override
    public EventDto viewEvent(Long eventId) {

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new ResourceNotFoundException("Event with id: " + eventId + " not found in database."));

        return mapToEventDto(event);
    }

    public EventDto mapToEventDto(Event event){

        return EventDto.builder()
                .eventId(event.getEventId())
                .calenderId(event.getCalendar().getCalendarId())
                .academicYear(event.getCalendar().getAcademicYear())
                .eventType(event.getEventType())
                .eventName(event.getEventName())
                .eventDate(event.getEventDate())
                .schoolClasses(event.getSchoolClasses().stream().map(SchoolClass::getClassName).toList())
                .sections(event.getSections().stream().map(Section::getSectionName).toList())
                .description(event.getDescription())
                .build();
    }

    public HolidayDto mapToHolidayDto(Holiday holiday){

        return HolidayDto.builder()
                .holidayId(holiday.getHolidayId())
                .calendarId(holiday.getCalendar().getCalendarId())
                .academicYear(holiday.getCalendar().getAcademicYear())
                .holidayDate(holiday.getHolidayDate())
                .holidayName(holiday.getHolidayName())
                .isEmergency(holiday.getIsEmergency())
                .rescheduleRequired(holiday.getRescheduleRequired())
                .rescheduledDate(holiday.getRescheduledDate())
                .timetableRescheduleDate(holiday.getTimetableRescheduleDate())
                .eventRescheduleDate(holiday.getEventRescheduleDate())
                .build();
    }

    public CalendarDto mapToCalenderDto(AcademicCalendar academicCalendar) {

        return CalendarDto.builder()
                .calendarId(academicCalendar.getCalendarId())
                .academicYear(academicCalendar.getAcademicYear())
                .startDate(academicCalendar.getStartDate())
                .endDate(academicCalendar.getEndDate())
                .createdBy(academicCalendar.getCreatedBy().getRole())
                .createdAt(academicCalendar.getCreatedAt())
                .updatedAt(academicCalendar.getUpdatedAt())
                .build();
    }
}

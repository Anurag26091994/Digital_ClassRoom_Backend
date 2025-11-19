package com.digital.serviceimpl;

import com.digital.dto.*;
import com.digital.entity.*;
import com.digital.enums.DayOfWeek;
import com.digital.enums.EventType;
import com.digital.enums.Role;
import com.digital.enums.Status;
import com.digital.events.EmergencyHolidayEvent;
import com.digital.events.RescheduledEvent;
import com.digital.events.RescheduledHolidayEvent;
import com.digital.exception.InvalidDateException;
import com.digital.exception.ResourceNotFoundException;
import com.digital.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarServiceImplTest {

    @Mock
    private AcademicCalendarRepository academicCalendarRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private HolidayRepository holidayRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ClassRepository classRepository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private TimetableRepository timetableRepository;

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private CalendarServiceImpl calendarService;

    private AcademicCalendar academicCalendar;

    private Year currentYear;

    private Year nextYear;

    private LocalDate futureDate;

    private Admin admin;

    @BeforeEach
    void setup(){

        currentYear = Year.now();
        nextYear = currentYear.plusYears(1);
        futureDate = LocalDate.now().plusDays(1);

        admin = Admin.builder()
                .adminId(1L)
                .role(Role.ADMIN)
                .username("admin@school.com")
                .password("Admin@123")
                .status(Status.ACTIVE)
                .build();

        academicCalendar = AcademicCalendar.builder()
                .calendarId(1L)
                .academicYear(currentYear + "-" + nextYear)
                .startDate(futureDate)
                .endDate(futureDate.plusMonths(7))
                .createdBy(admin)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateAcademicCalendar_success() {

        when(adminRepository.findByUsername("admin@school.com")).thenReturn(Optional.of(admin));

        when(academicCalendarRepository.save(any(AcademicCalendar.class)))
            .thenReturn(academicCalendar);

        CreateAcademicCalenderRequest request = CreateAcademicCalenderRequest.builder()
                .academicYear(currentYear + "-" + nextYear)
                .startDate(futureDate)
                .endDate(futureDate.plusMonths(7))
                .build();

        CalendarDto result = calendarService.createAcademicCalender(request, "admin@school.com");

        assertNotNull(result);
        assertEquals("ADMIN", result.getCreatedBy().name());
        verify(adminRepository, times(1)).findByUsername("admin@school.com");
        verify(academicCalendarRepository, times(1)).save(any(AcademicCalendar.class));
    }

    @Test
    void testCreateAcademicCalendar_ResourceNotFoundException_WhenAdminNotFound(){

        CreateAcademicCalenderRequest request = CreateAcademicCalenderRequest.builder()
                .academicYear(currentYear + "-" + nextYear)
                .startDate(futureDate)
                .endDate(futureDate.plusMonths(7))
                .build();

        when(adminRepository.findByUsername("missing@school.com")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                calendarService.createAcademicCalender(request, "missing@school.com"));

        assertEquals("Admin record not found in database", exception.getMessage());
        verify(academicCalendarRepository, never()).save(any(AcademicCalendar.class));
    }

    @Test
    void testCreateAcademicCalendar_InvalidDateException_WhenDateIsInvalid(){

        CreateAcademicCalenderRequest request = CreateAcademicCalenderRequest.builder()
                .academicYear(currentYear + "-" + nextYear)
                .startDate(futureDate.plusMonths(7))
                .endDate(futureDate)
                .build();

        when(adminRepository.findByUsername("admin@school.com")).thenReturn(Optional.of(admin));

        InvalidDateException exception = assertThrows(InvalidDateException.class, () ->
                calendarService.createAcademicCalender(request, "admin@school.com"));

        assertEquals("Start date must be before end date.", exception.getMessage());
        verify(adminRepository, times(1)).findByUsername("admin@school.com");
        verify(academicCalendarRepository, never()).save(any(AcademicCalendar.class));
    }

    @Test
    void updateAcademicCalender() {

        when(academicCalendarRepository.findById(1L)).thenReturn(Optional.of(academicCalendar));

        UpdateAcademicCalenderRequest request = UpdateAcademicCalenderRequest.builder()
                .academicYear(currentYear + "-" + nextYear)
                .startDate(futureDate)
                .endDate(futureDate.plusDays(364))
                .build();

        when(academicCalendarRepository.save(any(AcademicCalendar.class))).thenReturn(academicCalendar);

        CalendarDto result = calendarService.updateAcademicCalender(1L, request);

        assertNotNull(result);
        verify(academicCalendarRepository, times(1)).findById(1L);
        verify(academicCalendarRepository, times(1)).save(any(AcademicCalendar.class));
    }

    @Test
    void updateAcademicCalendar_TestResourceNotFoundException_WhenCalendarNotFound(){

        when(academicCalendarRepository.findById(2L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> calendarService.updateAcademicCalender(2L,
                        any(UpdateAcademicCalenderRequest.class)));

        assertEquals("Academic calendar record not found in database.", exception.getMessage());
        verify(academicCalendarRepository, times(1)).findById(2L);
    }

    @Test
    void getAcademicCalenders() {

        List<AcademicCalendar> list = new ArrayList<>();
        list.add(academicCalendar);
        list.add(academicCalendar);

        when(academicCalendarRepository.findAll()).thenReturn(list);

        List<CalendarDto> academicCalenders = calendarService.getAcademicCalenders();

        assertNotNull(academicCalenders);
        verify(academicCalendarRepository, times(1)).findAll();
    }

    @Test
    void getAcademicCalender() {

        when(academicCalendarRepository.findById(1L)).thenReturn(Optional.of(academicCalendar));

        CalendarDto result = calendarService.getAcademicCalender(1L);

        assertNotNull(result);
        verify(academicCalendarRepository,  times(1)).findById(1L);
    }

    @Test
    void getAcademicCalendar_ResourceNotFoundException_WhenCalendarNotFound(){

        when(academicCalendarRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                calendarService.getAcademicCalender(1L));

        assertEquals("Academic calendar record not found in database.", exception.getMessage());
        verify(academicCalendarRepository, times(1)).findById(1L);
    }

    @Test
    void addHoliday_Success_forNormalHoliday() {

        when(academicCalendarRepository.findById(1L)).thenReturn(Optional.of(academicCalendar));

        Holiday holiday = Holiday.builder()
                .holidayDate(futureDate)
                .holidayName("Diwali")
                .calendar(academicCalendar)
                .isEmergency(false)
                .rescheduleRequired(false)
                .rescheduledDate(null)
                .timetableRescheduleDate(null)
                .eventRescheduleDate(null)
                .build();

        when(holidayRepository.save(holiday)).thenReturn(holiday);

        HolidayDto result = calendarService.addHoliday(1L, holiday);

        assertNotNull(result);
        verify(holidayRepository, times(1)).save(any(Holiday.class));
    }

    @Test
    void addHoliday_Success_forEmergencyHoliday() {

        when(academicCalendarRepository.findById(1L)).thenReturn(Optional.of(academicCalendar));

        Holiday holiday = Holiday.builder()
                .holidayDate(futureDate)
                .holidayName("Diwali")
                .calendar(academicCalendar)
                .isEmergency(true)
                .rescheduleRequired(false)
                .rescheduledDate(null)
                .timetableRescheduleDate(futureDate)
                .eventRescheduleDate(futureDate.plusDays(1))
                .build();

        when(holidayRepository.save(holiday)).thenReturn(holiday);

        List<Student> students = new ArrayList<>();
        students.add(Student.builder().studentRegId(1L).build());
        students.add(Student.builder().studentRegId(2L).build());

        when(studentRepository.findAll()).thenReturn(students);

        List<Long> studentIds = students.stream().map(Student::getStudentRegId).toList();

        List<Teacher> teachers = new ArrayList<>();
        teachers.add(Teacher.builder().id(1L).build());
        teachers.add(Teacher.builder().id(2L).build());

        when(teacherRepository.findAll()).thenReturn(teachers);

        List<Long> teacherIds = teachers.stream().map(Teacher::getId).toList();

        publisher.publishEvent(new EmergencyHolidayEvent(holiday.getHolidayId(),
                holiday.getHolidayDate(), studentIds, teacherIds));

        Timetable timetable1 = Timetable.builder()
                .schoolClass(null)
                .section(null)
                .subject(null)
                .teacher(null)
                .date(holiday.getHolidayDate())
                .dayOfWeek(DayOfWeek.valueOf(holiday.getHolidayDate().getDayOfWeek().name()))
                .startTime(LocalDateTime.of(holiday.getHolidayDate(), LocalTime.of(11, 0, 0)))
                .endTime(LocalDateTime.of(holiday.getHolidayDate(), LocalTime.of(12, 0, 0)))
                .build();

        Timetable timetable2 = Timetable.builder()
                .schoolClass(null)
                .section(null)
                .subject(null)
                .teacher(null)
                .date(holiday.getHolidayDate())
                .dayOfWeek(DayOfWeek.valueOf(holiday.getHolidayDate().getDayOfWeek().name()))
                .startTime(LocalDateTime.of(holiday.getHolidayDate(), LocalTime.of(12, 0, 0)))
                .endTime(LocalDateTime.of(holiday.getHolidayDate(), LocalTime.of(13, 0, 0)))
                .build();

        List<Timetable> timetables = new ArrayList<>();
        timetables.add(timetable1);
        timetables.add(timetable2);

        when(timetableRepository.findAllByDate(holiday.getHolidayDate())).thenReturn(timetables);

        timetables.forEach(timetable -> {

            LocalTime startLocalTime = timetable.getStartTime().toLocalTime();
            LocalTime endLocalTime = timetable.getEndTime().toLocalTime();

            timetable.setDate(holiday.getTimetableRescheduleDate());
            timetable.setStartTime(LocalDateTime.of(holiday.getTimetableRescheduleDate(),
                    startLocalTime));
            timetable.setEndTime(LocalDateTime.of(holiday.getTimetableRescheduleDate(),
                    endLocalTime));
            String dayName = holiday.getTimetableRescheduleDate().getDayOfWeek().name();

            timetable.setDayOfWeek(DayOfWeek.valueOf(dayName));

            timetableRepository.save(timetable);
        });

        Session session1 = Session.builder()
                .schoolClass(null)
                .section(null)
                .teacher(null)
                .date(holiday.getHolidayDate())
                .startTime(LocalDateTime.of(holiday.getHolidayDate(), LocalTime.of(11, 0, 0)))
                .endTime(LocalDateTime.of(holiday.getHolidayDate(), LocalTime.of(12, 0, 0)))
                .topic("Integration")
                .description("Introduction to integration")
                .build();

        Session session2 = Session.builder()
                .schoolClass(null)
                .section(null)
                .teacher(null)
                .date(holiday.getHolidayDate())
                .startTime(LocalDateTime.of(holiday.getHolidayDate(), LocalTime.of(12, 0, 0)))
                .endTime(LocalDateTime.of(holiday.getHolidayDate(), LocalTime.of(13, 0, 0)))
                .topic("Interference")
                .description("Introduction to interference")
                .build();

        List<Session> sessions = new ArrayList<>();
        sessions.add(session1);
        sessions.add(session2);

        when(sessionRepository.findAllByDate(holiday.getHolidayDate())).thenReturn(sessions);

        sessions.forEach(session -> {

            LocalTime startLocalTime = session.getStartTime().toLocalTime();
            LocalTime endLocalTime = session.getEndTime().toLocalTime();

            session.setDate(holiday.getTimetableRescheduleDate());
            session.setStartTime(LocalDateTime.of(holiday.getTimetableRescheduleDate(), startLocalTime));
            session.setEndTime(LocalDateTime.of(holiday.getTimetableRescheduleDate(), endLocalTime));
            session.setJoinLink(null);

            when(sessionRepository.save(session)).thenReturn(session);
        });

        Event event1 = Event.builder()
                .eventId(1L)
                .eventName("EXAM")
                .schoolClasses(null)
                .eventType(EventType.Exam)
                .sections(null)
                .calendar(academicCalendar)
                .eventDate(holiday.getHolidayDate())
                .description("Exam is going t held")
                .build();

        List<Event> events = new ArrayList<>();
        events.add(event1);

        when(eventRepository.findAllByEventTypeAndEventDate(EventType.Exam, holiday.getHolidayDate())).thenReturn(events);

        events.forEach(event -> {
             event.setEventDate(holiday.getEventRescheduleDate());

             when(eventRepository.save(event)).thenReturn(event);

            publisher.publishEvent(new RescheduledEvent(event.getEventId(),
                    event.getEventName(), event.getEventDate(), event.getEventDate(),
                    studentIds, teacherIds));
        });

        HolidayDto result = calendarService.addHoliday(1L, holiday);

        assertNotNull(result);
        verify(holidayRepository, times(1)).save(any(Holiday.class));
        verify(studentRepository, times(1)).findAll();
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    void addHoliday_InvalidDateException(){

        when(academicCalendarRepository.findById(1L)).thenReturn(Optional.of(academicCalendar));

        Holiday holiday = Holiday.builder()
                .holidayDate(LocalDate.of(2024, 10, 10))
                .holidayName("Diwali")
                .calendar(academicCalendar)
                .isEmergency(false)
                .rescheduleRequired(false)
                .rescheduledDate(null)
                .build();

        InvalidDateException exception = assertThrows(InvalidDateException.class, () ->
                calendarService.addHoliday(1L, holiday));

        assertEquals("Holiday date must be within academic calendar year", exception.getMessage());
        verify(holidayRepository, never()).save(any(Holiday.class));
    }

    @Test
    void addHoliday_ResourceNotFound_WhenCalendarNotFound(){

        when(academicCalendarRepository.findById(1L)).thenReturn(Optional.empty());

        Holiday holiday = Holiday.builder()
                .holidayDate(futureDate)
                .holidayName("Diwali")
                .calendar(academicCalendar)
                .isEmergency(false)
                .rescheduleRequired(false)
                .rescheduledDate(null)
                .build();

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                calendarService.addHoliday(1L, holiday));

        assertEquals("Academic calendar record not found in database", exception.getMessage());
        verify(holidayRepository, never()).save(any(Holiday.class));
    }

    @Test
    void updateHoliday() {

        Holiday holiday = Holiday.builder()
                .holidayDate(futureDate)
                .holidayName("Diwali")
                .calendar(academicCalendar)
                .isEmergency(false)
                .rescheduleRequired(false)
                .rescheduledDate(null)
                .build();

        when(holidayRepository.findById(1L)).thenReturn(Optional.of(holiday));

        when(academicCalendarRepository.findById(1L)).thenReturn(Optional.of(academicCalendar));

        holiday.setCalendar(academicCalendar);
        holiday.setHolidayName(holiday.getHolidayName());
        holiday.setHolidayDate(futureDate);
        holiday.setIsEmergency(holiday.getIsEmergency());
        holiday.setRescheduleRequired(true);
        holiday.setRescheduledDate(futureDate);

        when(holidayRepository.save(holiday)).thenReturn(holiday);

        List<Student> students = new ArrayList<>();
        students.add(Student.builder().studentRegId(1L).build());
        students.add(Student.builder().studentRegId(2L).build());

        when(studentRepository.findAll()).thenReturn(students);

        List<Long> studentIds = students.stream().map(Student::getStudentRegId).toList();

        List<Teacher> teachers = new ArrayList<>();
        teachers.add(Teacher.builder().id(1L).build());
        teachers.add(Teacher.builder().id(2L).build());

        when(teacherRepository.findAll()).thenReturn(teachers);

        List<Long> teacherIds = teachers.stream().map(Teacher::getId).toList();

        publisher.publishEvent(new RescheduledHolidayEvent(holiday.getHolidayId(),
                holiday.getHolidayName(), holiday.getHolidayDate(),
                holiday.getRescheduledDate(), studentIds, teacherIds));

        HolidayDto result = calendarService.updateHoliday(1L, holiday);

        assertNotNull(result);
        verify(holidayRepository, times(1)).findById(1L);
        verify(holidayRepository, times(1)).save(any(Holiday.class));
        verify(studentRepository, times(1)).findAll();
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    void updateHoliday_ResourceNotFoundException_WhenHolidayNotFound(){

        Holiday holiday = Holiday.builder()
                .holidayId(1L)
                .calendar(academicCalendar)
                .isEmergency(false)
                .rescheduleRequired(false)
                .rescheduledDate(null)
                .build();

        when(holidayRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                calendarService.updateHoliday(1L, holiday));

        assertEquals("Holiday record not found in database.", exception.getMessage());
        verify(holidayRepository, times(1)).findById(1L);
        verify(holidayRepository, never()).save(any(Holiday.class));
        verify(studentRepository, never()).findAll();
        verify(teacherRepository, never()).findAll();
        verify(publisher, never()).publishEvent(any(RescheduledHolidayEvent.class));
    }

    @Test
    void updateHoliday_ResourceNotFoundException_WhenCalendarNotFound(){

        Holiday holiday = Holiday.builder().holidayId(1L)
                .calendar(academicCalendar)
                .build();

        when(holidayRepository.findById(1L)).thenReturn(Optional.of(holiday));

        when(academicCalendarRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                calendarService.updateHoliday(1L, holiday));

        assertEquals("Academic calendar record not found in database.", exception.getMessage());
        verify(holidayRepository, times(1)).findById(1L);
        verify(holidayRepository, never()).save(any(Holiday.class));
        verify(studentRepository, never()).findAll();
        verify(teacherRepository, never()).findAll();
        verify(publisher, never()).publishEvent(any(RescheduledHolidayEvent.class));
    }

    @Test
    void removeHoliday() {

        when(holidayRepository.findById(1L)).thenReturn(Optional.of(Holiday.builder().holidayId(1L)
                .calendar(academicCalendar)
                .build()));

        String message = calendarService.removeHoliday(1L);

        assertEquals("Holiday record deleted successfully.", message);
        verify(holidayRepository, times(1)).findById(1L);
        verify(holidayRepository, times(1)).delete(any(Holiday.class));
    }

    @Test
    void removeHoliday_ResourceNotFoundException_WhenHolidayNotFound(){

        when(holidayRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                calendarService.removeHoliday(1L));

        assertEquals("Holiday record not found in database.", exception.getMessage());
        verify(holidayRepository, times(1)).findById(1L);
        verify(holidayRepository, never()).delete(any(Holiday.class));
    }

    @Test
    void viewHoliday(){

        when(holidayRepository.findById(1L)).thenReturn(Optional.of(Holiday.builder().holidayId(1L)
                .calendar(academicCalendar).build()));

        HolidayDto result = calendarService.viewHoliday(1L);

        assertNotNull(result);
        verify(holidayRepository, times(1)).findById(anyLong());
    }

    @Test
    void viewHoliday_ResourceNotFoundException_WhenHolidayNotFound(){

        when(holidayRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                calendarService.viewHoliday(1L));

        assertEquals("Holiday record with id: " + 1L + " not found in database", exception.getMessage());
        verify(holidayRepository, times(1)).findById(anyLong());
    }

    @Test
    void viewHolidays(){

        List<Holiday> holidays = List.of(Holiday.builder().calendar(academicCalendar).build());

        when(holidayRepository.findAll()).thenReturn(holidays);

        LocalDate currentDate = LocalDate.now();

        List<HolidayDto> result = calendarService.viewHolidays();

        assertNotNull(result);
        verify(holidayRepository, times(1)).findAll();
    }
    
    @Test
    void addEvent() {

        when(academicCalendarRepository.findById(1L)).thenReturn(Optional.of(academicCalendar));

        when(classRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(SchoolClass.builder().classId(1L).build(), SchoolClass.builder().classId(2L).build()));

        when(sectionRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(Section.builder().sectionId(1L).build(), Section.builder().sectionId(2L).build()));

        Event event = Event.builder()
                .eventId(1L)
                .calendar(academicCalendar)
                .eventName("Unit tests")
                .eventDate(futureDate)
                .schoolClasses(List.of(SchoolClass.builder().classId(1L).build(), SchoolClass.builder().classId(2L).build()))
                .sections(List.of(Section.builder().sectionId(1L).build(), Section.builder().sectionId(2L).build()))
                .build();

        when(eventRepository.save(event)).thenReturn(event);

        EventDto result = calendarService.addEvent(1L, event);

        assertNotNull(result);
        verify(academicCalendarRepository, times(1)).findById(anyLong());
        verify(classRepository, times(1)).findAllById(anyList());
        verify(sectionRepository, times(1)).findAllById(anyList());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void viewCalenderEvents() {

        LocalDate currentDate = LocalDate.now();

        Event event1 = Event.builder()
                .eventId(1L)
                .calendar(academicCalendar)
                .eventName("Unit tests")
                .eventDate(futureDate)
                .schoolClasses(List.of(SchoolClass.builder().classId(1L).build(), SchoolClass.builder().classId(2L).build()))
                .sections(List.of(Section.builder().sectionId(1L).build(), Section.builder().sectionId(2L).build()))
                .build();

        Event event2 = Event.builder()
                .eventId(2L)
                .calendar(academicCalendar)
                .eventName("Workshop")
                .eventDate(futureDate.plusDays(1))
                .schoolClasses(List.of(SchoolClass.builder().classId(1L).build(), SchoolClass.builder().classId(2L).build()))
                .sections(List.of(Section.builder().sectionId(1L).build(), Section.builder().sectionId(2L).build()))
                .build();

        List<Event> list = List.of(event1, event2);

        when(eventRepository.findAll()).thenReturn(list);

        List<EventDto> result = calendarService.viewCalenderEvents();

        assertNotNull(result);
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void removeEvent() {

        Event event = Event.builder()
                .eventId(1L)
                .calendar(academicCalendar)
                .eventName("Unit tests")
                .eventDate(futureDate)
                .schoolClasses(List.of(SchoolClass.builder().classId(1L).build(), SchoolClass.builder().classId(2L).build()))
                .sections(List.of(Section.builder().sectionId(1L).build(), Section.builder().sectionId(2L).build()))
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        String result = calendarService.removeEvent(1L);

        assertEquals("Event record deleted successfully.", result);
        verify(eventRepository, times(1)).findById(anyLong());
        verify(eventRepository, times(1)).delete(any(Event.class));
    }

    @Test
    void removeEvent_ResourceNotFoundException_WhenEventNotFound(){

        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> calendarService.removeEvent(1L));

        assertEquals("Event record not found in database.", exception.getMessage());
        verify(eventRepository, times(1)).findById(anyLong());
        verify(eventRepository, never()).delete(any(Event.class));
    }

    @Test
    void updateEvent() {

        Event existingEvent = Event.builder()
                .eventId(1L)
                .calendar(academicCalendar)
                .eventName("Unit tests")
                .eventDate(futureDate)
                .schoolClasses(List.of(SchoolClass.builder().classId(1L).build(), SchoolClass.builder().classId(2L).build()))
                .sections(List.of(Section.builder().sectionId(1L).build(), Section.builder().sectionId(2L).build()))
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));

        when(academicCalendarRepository.findById(1L)).thenReturn(Optional.of(academicCalendar));

        List<SchoolClass> classes = List.of(SchoolClass.builder().classId(1L).build(), SchoolClass.builder().classId(2L).build());

        when(classRepository.findAllById(List.of(1L, 2L))).thenReturn(classes);

        List<Section> sections = List.of(Section.builder().sectionId(1L).build(), Section.builder().sectionId(2L).build());

        when(sectionRepository.findAllById(List.of(1L, 2L))).thenReturn(sections);

        Event updatedEvent = Event.builder()
                .eventId(existingEvent.getEventId())
                .calendar(existingEvent.getCalendar())
                .eventName("Unit tests for classes")
                .eventDate(futureDate.plusDays(1))
                .description("There will be having unit test")
                .schoolClasses(existingEvent.getSchoolClasses())
                .sections(existingEvent.getSections())
                .build();

        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);

        List<Student> students = List.of(Student.builder().studentRegId(1L).build(), Student.builder().studentRegId(2L).build());

        when(studentRepository.findAll()).thenReturn(students);

        List<Teacher> teachers = List.of(Teacher.builder().id(1L).build(), Teacher.builder().id(2L).build());

        when(teacherRepository.findAll()).thenReturn(teachers);

        publisher.publishEvent(new RescheduledEvent(1L, "Unit Tests for classes", futureDate.plusDays(1), futureDate.plusDays(1),
                List.of(1L, 2L), List.of(1L, 2L)));

        EventDto result = calendarService.updateEvent(1L, updatedEvent);

        assertNotNull(result);
        verify(eventRepository, times(1)).findById(anyLong());
        verify(eventRepository, times(1)).save(any(Event.class));
        verify(academicCalendarRepository, times(1)).findById(anyLong());
        verify(classRepository, times(1)).findAllById(anyList());
        verify(sectionRepository, times(1)).findAllById(anyList());
        verify(studentRepository, times(1)).findAll();
        verify(teacherRepository, times(1)).findAll();
        verify(publisher, times(1)).publishEvent(any(RescheduledEvent.class));
    }

    @Test
    void updateEvent_ResourceNotFoundException_WhenEventNotFound(){

        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> calendarService.removeEvent(1L));

        assertEquals("Event record not found in database.", exception.getMessage());
        verify(eventRepository, times(1)).findById(anyLong());
        verify(eventRepository, never()).save(any(Event.class));
        verify(academicCalendarRepository, never()).findById(anyLong());
        verify(classRepository, never()).findAllById(anyList());
        verify(sectionRepository, never()).findAllById(anyList());
        verify(studentRepository, never()).findAll();
        verify(teacherRepository, never()).findAll();
        verify(publisher, never()).publishEvent(any(RescheduledEvent.class));
    }

    @Test
    void updateEvent_ResourceNotFoundException_WhenCalendarNotFound(){

        Event existingEvent = Event.builder()
                .eventId(1L)
                .calendar(academicCalendar)
                .eventName("Unit tests")
                .eventDate(futureDate)
                .schoolClasses(List.of(SchoolClass.builder().classId(1L).build(), SchoolClass.builder().classId(2L).build()))
                .sections(List.of(Section.builder().sectionId(1L).build(), Section.builder().sectionId(2L).build()))
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));

        when(academicCalendarRepository.findById(1L)).thenReturn(Optional.empty());

        Event updatedEvent = Event.builder()
                .eventId(existingEvent.getEventId())
                .calendar(existingEvent.getCalendar())
                .eventName("Unit tests for classes")
                .eventDate(futureDate.plusDays(1))
                .description("There will be having unit test")
                .schoolClasses(existingEvent.getSchoolClasses())
                .sections(existingEvent.getSections())
                .build();

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                calendarService.updateEvent(1L, updatedEvent));

        assertEquals("Academic calendar record not found in database", exception.getMessage());
        verify(eventRepository, times(1)).findById(1L);
        verify(academicCalendarRepository, times(1)).findById(anyLong());
        verify(eventRepository, never()).save(any(Event.class));
        verify(classRepository, never()).findAllById(anyList());
        verify(sectionRepository, never()).findAllById(anyList());
        verify(studentRepository, never()).findAll();
        verify(teacherRepository, never()).findAll();
        verify(publisher, never()).publishEvent(any(RescheduledEvent.class));
    }

    @Test
    void viewEvent(){

        Event existingEvent = Event.builder()
                .eventId(1L)
                .calendar(academicCalendar)
                .eventName("Unit tests")
                .eventDate(futureDate)
                .schoolClasses(List.of(SchoolClass.builder().classId(1L).build(), SchoolClass.builder().classId(2L).build()))
                .sections(List.of(Section.builder().sectionId(1L).build(), Section.builder().sectionId(2L).build()))
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));

        EventDto result = calendarService.viewEvent(1L);

        assertNotNull(result);
        verify(eventRepository, times(1)).findById(anyLong());
    }

    @Test
    void viewEvent_ResourceNotFoundException_WhenEventNotFound(){

        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> calendarService.viewEvent(1L));

        assertEquals("Event with id: " + 1L + " not found in database.", exception.getMessage());
        verify(eventRepository, times(1)).findById(anyLong());
    }

}
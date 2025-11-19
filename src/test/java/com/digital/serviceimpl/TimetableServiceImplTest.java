package com.digital.serviceimpl;

import com.digital.dto.TimetableRequest;
import com.digital.dto.UpdateTimetableRequest;
import com.digital.entity.*;
import com.digital.enums.DayOfWeek;
import com.digital.exception.ResourceNotFoundException;
import com.digital.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TimetableServiceImplTest {

    @InjectMocks
    private TimetableServiceImpl timetableService;

    @Mock
    private TimetableRepository timetableRepository;
    @Mock
    private ClassRepository classRepository;
    @Mock
    private SectionRepository sectionRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private SessionServiceImpl sessionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ------------------ CREATE ------------------
    @Test
    void testCreateTimetable_Success() {
        // Arrange
        TimetableRequest request = new TimetableRequest();
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setClassId(1L);
        request.setSchoolClass(schoolClass);

        Section section = new Section();
        section.setSectionId(1L);
        request.setSection(section);

        Subject subject = new Subject();
        subject.setSubjectId(1L);
        request.setSubject(subject);

        Teacher teacher = new Teacher();
        teacher.setId(1L);
        request.setTeacher(teacher);

        request.setStartTime(LocalDateTime.now().plusDays(1));
        request.setEndTime(LocalDateTime.now().plusDays(1).plusHours(1));
        request.setDate(LocalDate.now());
        request.setDayOfWeek(DayOfWeek.MONDAY);
        request.setTopic("Topic 1");
        request.setDescription("Description 1");

        when(classRepository.findById(1L)).thenReturn(Optional.of(schoolClass));
        when(sectionRepository.findById(1L)).thenReturn(Optional.of(section));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(timetableRepository.existsBySection_SectionIdAndStartTime(anyLong(), any()))
                .thenReturn(false);
        when(timetableRepository.existsByStartTimeAndTeacher_Id(any(), anyLong()))
                .thenReturn(false);

        when(timetableRepository.save(any(Timetable.class))).thenAnswer(i -> i.getArgument(0));
        when(sessionService.createSession(any(Session.class))).thenReturn(new Session());

        // Act
        String result = timetableService.createTimetable(request);

        // Assert
        assertEquals("Timetable and Session created successfully", result);
    }

    @Test
    void testCreateTimetable_ClassNotFound() {
        TimetableRequest request = new TimetableRequest();
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setClassId(1L);
        request.setSchoolClass(schoolClass);

        when(classRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> timetableService.createTimetable(request));
        assertTrue(ex.getMessage().contains("Class with id"));
    }

    // ------------------ UPDATE ------------------
    @Test
    void testUpdateTimetable_Success() {
        Long timetableId = 1L;

        Timetable existing = new Timetable();
        existing.setStartTime(LocalDateTime.now().plusDays(1));
        existing.setEndTime(LocalDateTime.now().plusDays(1).plusHours(1));

        when(timetableRepository.findById(timetableId)).thenReturn(Optional.of(existing));

        UpdateTimetableRequest request = new UpdateTimetableRequest();
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        request.setTeacher(teacher);

        Subject subject = new Subject();
        subject.setSubjectId(1L);
        request.setSubject(subject);

        request.setStartTime(LocalDateTime.now().plusDays(2));
        request.setEndTime(LocalDateTime.now().plusDays(2).plusHours(1));
        request.setTopic("Topic");
        request.setDescription("Desc");

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));

        Session session = new Session();
        session.setSessionId(1L);
        when(sessionRepository.findByTimetable_TimetableId(timetableId)).thenReturn(session);

        when(timetableRepository.save(any(Timetable.class))).thenReturn(existing);
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        String result = timetableService.updateTimetable(timetableId, request);
        assertTrue(result.contains("updated successfully"));
    }

    // ------------------ GET ------------------
    @Test
    void testGetTimetables() {
        when(timetableRepository.findAll()).thenReturn(List.of(new Timetable(), new Timetable()));
        List<Timetable> result = timetableService.getTimetables();
        assertEquals(2, result.size());
    }

    // ------------------ DELETE ------------------
    @Test
    void testDeleteTimetable_Success() {
        Long timetableId = 1L;
        Timetable timetable = new Timetable();
        timetable.setStartTime(LocalDateTime.now().plusDays(1));

        Session session = new Session();
        session.setSessionId(1L);

        when(timetableRepository.findById(timetableId)).thenReturn(Optional.of(timetable));
        when(sessionRepository.findByTimetable_TimetableId(timetableId)).thenReturn(session);

        String result = timetableService.deleteTimetable(timetableId);
        assertTrue(result.contains("deleted successfully"));

        verify(sessionRepository).delete(session);
        verify(timetableRepository).delete(timetable);
    }

    // ------------------ STUDENT TIMETABLE ------------------
    @Test
    void testGetStudentTimetableBySectionId() {
        when(timetableRepository.findAllBySection_SectionId(1L))
                .thenReturn(List.of(new Timetable(), new Timetable(), new Timetable()));

        List<Timetable> result = timetableService.getStudentTimetableBySectionId(1L);
        assertEquals(3, result.size());
    }
}

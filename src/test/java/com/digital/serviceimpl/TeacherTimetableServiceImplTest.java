package com.digital.serviceimpl;

import com.digital.entity.Timetable;
import com.digital.repository.TimetableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherTimetableServiceImplTest {

    @Mock
    private TimetableRepository timetableRepository;

    @InjectMocks
    private TeacherTimetableServiceImpl teacherTimetableService;

    private List<Timetable> timetableList;

    @BeforeEach
    void setUp() {
        Timetable t1 = new Timetable();
        t1.setStartTime(null); // set some data if needed
        Timetable t2 = new Timetable();
        t2.setStartTime(null);

        timetableList = Arrays.asList(t1, t2);
    }

    @Test
    void testGetTimetableByTeacherId() {
        Long teacherId = 1L;

        // Mock repository
        when(timetableRepository.findAllByTeacher_Id(teacherId)).thenReturn(timetableList);

        // Call service
        List<Timetable> result = teacherTimetableService.getTimetableByTeacherId(teacherId);

        // Verify
        assertEquals(2, result.size());
        assertEquals(timetableList, result);
    }
}

package com.digital.servicei;

import com.digital.entity.Timetable;

import java.util.List;

public interface TeacherTimetableService {

    List<Timetable> getTimetableByTeacherId(Long id);
}

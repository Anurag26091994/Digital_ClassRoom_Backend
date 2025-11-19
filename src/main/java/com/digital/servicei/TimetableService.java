package com.digital.servicei;

import com.digital.dto.TimetableRequest;
import com.digital.dto.UpdateTimetableRequest;
import com.digital.entity.Timetable;

import java.util.List;

public interface TimetableService {
    String createTimetable(TimetableRequest timetableRequest);

    String updateTimetable(Long timetableId, UpdateTimetableRequest request);

    List<Timetable> getTimetables();

    String deleteTimetable(Long timetableId);

    List<Timetable> getStudentTimetableBySectionId(Long sectionId);
}

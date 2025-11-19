package com.digital.serviceimpl;

import com.digital.entity.Timetable;
import com.digital.repository.TimetableRepository;
import com.digital.servicei.TeacherTimetableService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TeacherTimetableServiceImpl implements TeacherTimetableService {

    private final TimetableRepository timetableRepository;

    @Override
    public List<Timetable> getTimetableByTeacherId(Long id) {

        return timetableRepository.findAllByTeacher_Id(id);
    }
}

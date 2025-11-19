package com.digital.repository;

import com.digital.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findAllBySchoolClass_ClassId(Long classId);

    List<Session> findAllByTeacher_Id(Long id);

    List<Session> findAllBySection_SectionId(Long sectionId);

    Session findByTimetable_TimetableId(Long timetableId);

    List<Session> findAllByDate(LocalDate timetableRescheduleDate);
}

package com.digital.repository;

import com.digital.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    List<Timetable> findAllByTeacher_Id(Long id);

    List<Timetable> findAllBySection_SectionId(Long sectionId);

    boolean existsBySection_SectionIdAndStartTime(Long sectionId, LocalDateTime startTime);

    boolean existsByStartTimeAndTeacher_Id(LocalDateTime startTime, Long id);

    List<Timetable> findAllByDate(LocalDate holidayDate);
}

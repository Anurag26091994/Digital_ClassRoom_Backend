package com.digital.repository;

import com.digital.entity.AcademicCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcademicCalendarRepository extends JpaRepository<AcademicCalendar, Long> {


    boolean existsByAcademicYear(String academicYear);
}

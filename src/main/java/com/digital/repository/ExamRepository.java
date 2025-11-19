package com.digital.repository;

import com.digital.entity.Exam;
import com.digital.entity.SchoolClass;
import com.digital.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    // Custom query to check overlapping exams
    @Query("SELECT e FROM Exam e " +
            "WHERE e.schoolClass.classId = :classId AND e.section.sectionId = :sectionId " +
            "AND ((e.startTime < :endTime) AND (e.endTime > :startTime))")
    List<Exam> findOverlappingExams(@Param("classId") Long classId,
                                    @Param("sectionId") Long sectionId,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime);

    // Fetch all exams by teacher
    List<Exam> findByTeacher_Id(Long teacherId);

    // Fetch all exams by class and section
    List<Exam> findBySchoolClass_ClassIdAndSection_SectionId(Long classId, Long sectionId);

    // Optional filters
    List<Exam> findBySchoolClass_ClassId(Long classId);
    List<Exam> findBySubject_SubjectId(Long subjectId);

    // Fetch exams for a class & section after a given date/time
    List<Exam> findBySchoolClass_ClassIdAndSection_SectionIdAndStartTimeAfter(
            Long classId, Long sectionId, LocalDateTime now);

    // Fetch exams for a class & section after a given start time
    List<Exam> findBySchoolClassAndSectionAndStartTimeAfter(
            SchoolClass schoolClass, Section section, LocalDateTime fromTime
    );

}

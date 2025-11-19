package com.digital.repository;

import com.digital.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    boolean existsByEmail(String email);

    Optional<Teacher> findByEmail(String email); // <-- Add this findByTeacherId
   // List<Teacher> findByTeacherId(Long id);

    Optional<Teacher> findByUser_Username(String username);

    List<Teacher> findByAssignedClass_ClassId(Long classId);

    List<Teacher> findByAssignedClass_ClassIdAndAssignedSection_SectionId(Long classId, Long sectionId);
    List<Teacher> findAllByAssignedClass_ClassId(Long classId);

    Teacher findByUser_UserId(Long userId);
}

package com.digital.repository;

import com.digital.entity.ParentStudentMapping;
import com.digital.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParentStudentMappingRepository extends JpaRepository<ParentStudentMapping, Long> {
    @Query("SELECT m FROM ParentStudentMapping m WHERE m.parent.parentId = :parentId")
    List<ParentStudentMapping> findByParentId(@Param("parentId") Long parentId);

    // Changed to use Student entity
    List<ParentStudentMapping> findByStudent(Student student);

    // Check if a parent is linked to a specific student
    boolean existsByParent_ParentIdAndStudent_StudentRegId(Long parentId, Long studentRegId);

    // Fetch all mappings for a parent
    List<ParentStudentMapping> findByParent_ParentId(Long parentId);

    @Query("SELECT psm.student.studentRegId FROM ParentStudentMapping psm WHERE psm.parent.parentId = :parentId")
    List<Long> findStudentRegIdByParentId(Long parentId);

    List<ParentStudentMapping> findByStudent_StudentRegId(Long studentRegId);

}

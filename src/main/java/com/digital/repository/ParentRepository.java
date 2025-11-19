package com.digital.repository;

import com.digital.entity.Parent;
import com.digital.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Long> {
    Optional<Parent> findByParentId(Long parentId);
    // Optional<Parent> findByUserId(Long userId);
    //boolean existsByUserId(Long userId);
    boolean existsByUserUserId(Long userId);
    // Fetch parents by their child's studentId
    //List<Parent> findByStudent_StudentRegId(Long studentId);
    Optional<Parent> findByUser_UserId(Long userId);
    Optional<Parent> findByUser(User user);
    @Query("SELECT p FROM Parent p JOIN p.studentMappings m WHERE m.student.studentRegId = :studentRegId")
    List<Parent> findByStudentRegId(@Param("studentRegId") Long studentRegId);


}

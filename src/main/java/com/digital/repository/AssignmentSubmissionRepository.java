package com.digital.repository;

import com.digital.entity.AssignmentSubmission;
import com.digital.enums.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {

    Optional<AssignmentSubmission> findByAssignment_AssignmentIdAndStudentId(Long assignmentId, Long studentId);

    boolean existsByAssignment_AssignmentIdAndStudentId(Long assignmentId, Long studentId);


    List<AssignmentSubmission> findByStudentId(Long studentId);
}

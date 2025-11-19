package com.digital.repository;

import com.digital.entity.ExamSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamSubmissionRepository extends JpaRepository<ExamSubmission, Long> {

    // Check if submission exists
    boolean existsByExam_ExamIdAndStudent_StudentRegId(Long examId, Long studentRegId);

    // Find a specific submission
    Optional<ExamSubmission> findByExam_ExamIdAndStudent_StudentRegId(Long examId, Long studentRegId);

    // Find all submissions for an exam
    List<ExamSubmission> findByExam_ExamId(Long examId);

    Optional<ExamSubmission> findByStudent_User_UserIdAndExam_ExamId(Long studentId, Long examId);
    // Optional<ExamSubmission> findByAssignment_AssignmentIdAndStudent_StudentId(Long assignmentId, Long studentId);

}

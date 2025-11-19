package com.digital.repository;

import com.digital.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<ExamSubmission, Long> {
    // All submissions for a given exam
    List<ExamSubmission> findByExam_ExamId(Long examId);

    // Specific submission by exam and student
    Optional<ExamSubmission> findByExam_ExamIdAndStudent_StudentRegId(Long examId, Long studentRegId);

    Optional<ExamSubmission> findByExamAndStudent_StudentRegId(Exam exam, Long studentRegId);

}

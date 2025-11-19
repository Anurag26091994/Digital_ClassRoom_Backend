package com.digital.repository;

import com.digital.entity.Result;
import com.digital.enums.ResultStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {
    // Fetch all results for a student
    List<Result> findByStudent_StudentRegId(Long studentRegId);


    // Fetch a specific result for a student and exam
    Optional<Result> findByStudent_StudentRegIdAndExam_ExamId(Long studentRegId, Long examId);

    // Fetch all results for an exam
    List<Result> findByExam_ExamId(Long examId);

    // Optional: fetch by student and result status
    List<Result> findByStudent_StudentRegIdAndStatus(Long studentRegId, ResultStatus status);
    List<Result> findByExam_Teacher_Id(Long teacherId);
}
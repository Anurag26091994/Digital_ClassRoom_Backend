package com.digital.repository;

import com.digital.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    boolean existsBySubjectCode(String subjectCode);
}

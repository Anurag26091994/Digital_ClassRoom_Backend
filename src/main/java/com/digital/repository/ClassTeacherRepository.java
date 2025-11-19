package com.digital.repository;

import com.digital.entity.ClassTeacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassTeacherRepository extends JpaRepository<ClassTeacher, Long> {
}

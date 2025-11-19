package com.digital.repository;

import com.digital.entity.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<SchoolClass, Long> {
    boolean existsByClassName(String className);
}

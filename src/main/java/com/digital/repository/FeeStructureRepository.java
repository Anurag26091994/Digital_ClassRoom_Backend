package com.digital.repository;

import com.digital.entity.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {

    List<FeeStructure> findAllByClassId(Long classId);

    List<FeeStructure> findAllByClassIdAndAcademicYear(Long classId, String academicYear);
    Optional<FeeStructure> findByClassIdAndAcademicYear(Long classId, String academicYear);

    List<FeeStructure> findByClassId(Long classId);


}

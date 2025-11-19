package com.digital.repository;

import com.digital.entity.SchoolClass;
import com.digital.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    boolean existsBySchoolClassAndSectionName(SchoolClass schoolClass, String sectionName);

}

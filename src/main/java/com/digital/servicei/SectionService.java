package com.digital.servicei;

import com.digital.dto.SectionRequest;
import com.digital.dto.SectionResponse;
import com.digital.entity.Section;

import java.util.List;

public interface SectionService {



    Section createSection(Long classId, Section section);
    Section updateSection(Long sectionId, Section section);
    void deleteSection(Long sectionId);
    Section getSectionById(Long sectionId);
    List<Section> getAllSections();
}

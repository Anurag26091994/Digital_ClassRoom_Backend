package com.digital.serviceimpl;

import com.digital.dto.SectionRequest;
import com.digital.dto.SectionResponse;
import com.digital.entity.SchoolClass;
import com.digital.entity.Section;
import com.digital.repository.ClassRepository;
import com.digital.repository.SectionRepository;
import com.digital.servicei.SectionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectionServiceImpl implements SectionService {
    private final SectionRepository sectionRepository;
    private final ClassRepository classRepository;

    public SectionServiceImpl(SectionRepository sectionRepository, ClassRepository classRepository) {
        this.sectionRepository = sectionRepository;
        this.classRepository = classRepository;
    }

//    @Override
//    public Section createSection(Long classId, Section section) {
//        SchoolClass schoolClass = classRepository.findById(classId)
//                .orElseThrow(() -> new RuntimeException("Class not found"));
//
//        if(sectionRepository.existsBySchoolClassAndSectionName(schoolClass, section.getSectionName()))
//            throw new RuntimeException("Section name already exists for this class");
//
//        section.setSchoolClass(schoolClass);
//        return sectionRepository.save(section);
//    }

    @Override
    @Transactional
    public Section createSection(Long classId, Section section) {
        SchoolClass schoolClass = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        if (sectionRepository.existsBySchoolClassAndSectionName(schoolClass, section.getSectionName())) {
            throw new RuntimeException("Section name already exists for this class");
        }

        section.setSchoolClass(schoolClass);
        return sectionRepository.save(section);
    }


    @Override
    public Section updateSection(Long sectionId, Section section) {
        Section existing = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));

        existing.setSectionName(section.getSectionName());
        existing.setCapacity(section.getCapacity());
        return sectionRepository.save(existing);
    }

    @Override
    public void deleteSection(Long sectionId) {
        if(!sectionRepository.existsById(sectionId))
            throw new RuntimeException("Section not found");
        sectionRepository.deleteById(sectionId);
    }

    @Override
    public Section getSectionById(Long sectionId) {
        return sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));
    }

    @Override
    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

}

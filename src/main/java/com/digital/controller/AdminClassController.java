package com.digital.controller;

import com.digital.dto.ClassRequest;
import com.digital.dto.ClassResponse;
import com.digital.dto.SectionResponse;
import com.digital.entity.SchoolClass;
import com.digital.entity.Section;
import com.digital.servicei.ClassService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/class")
@CrossOrigin(origins = "*") // Allow requests from any domain
public class AdminClassController {

    private final ClassService classService;

    public AdminClassController(ClassService classService) {
        this.classService = classService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<SchoolClass> createClass(@Valid @RequestBody SchoolClass schoolClass) {
        return ResponseEntity.ok(classService.createClass(schoolClass));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ClassResponse> updateClass(@Valid @PathVariable Long id,
                                                     @RequestBody ClassRequest request) {
        SchoolClass schoolClass = SchoolClass.builder()
                .className(request.getClassName())
                .description(request.getDescription())
                .build();
        SchoolClass updated = classService.updateClass(id, schoolClass);
        return ResponseEntity.ok(toResponse(updated));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.ok("Class deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping(value = "/fetch-all")
    public ResponseEntity<List<ClassResponse>> getAllClasses() {
        List<ClassResponse> list = classService.getAllClasses().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    private ClassResponse toResponse(SchoolClass schoolClass) {
        return ClassResponse.builder()
                .classId(schoolClass.getClassId())
                .className(schoolClass.getClassName())
                .description(schoolClass.getDescription())
                .createdAt(schoolClass.getCreatedAt())
                .updatedAt(schoolClass.getUpdatedAt())
                .sections(
                        schoolClass.getSections() == null
                                ? List.of()
                                : schoolClass.getSections().stream()
                                .map(this::toSectionResponse)
                                .collect(Collectors.toList())
                )
                .build();
    }

    private SectionResponse toSectionResponse(Section section) {
        return SectionResponse.builder()
                .sectionId(section.getSectionId())
                .sectionName(section.getSectionName())
                .createdAt(section.getCreatedAt())
                .updatedAt(section.getUpdatedAt())
                .build();
    }

}

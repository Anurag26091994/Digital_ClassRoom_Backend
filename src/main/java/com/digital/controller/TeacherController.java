package com.digital.controller;

import com.digital.dto.AssignTeacherRequest;
import com.digital.dto.AssignedTeacherResponse;
import com.digital.dto.TeacherCreateRequest;
import com.digital.dto.TeacherDto;
import com.digital.entity.Teacher;
import com.digital.servicei.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    // ✅ CREATE
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createTeacher(@RequestBody TeacherCreateRequest request) {
        try {
            TeacherDto createdTeacher = teacherService.createTeacher(request);
            return ResponseEntity.ok(createdTeacher);
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(Map.of("error", "Failed to create teacher: " + e.getMessage()));
        }
    }


    // ✅ READ by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getTeacherById(@PathVariable Long id) {
        try {
            TeacherDto teacher = teacherService.getTeacherById(id);
            return ResponseEntity.ok(teacher);
        } catch (Exception e) {
            return ResponseEntity
                    .status(404)
                    .body(Map.of("error", "Teacher not found with ID: " + id));
        }
    }

    // ✅ READ ALL
    @GetMapping(value = "/fetch-all")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<?> getAllTeachers() {
        try {
            List<TeacherDto> teachers = teacherService.getAllTeachers();
            return ResponseEntity.ok(teachers);
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(Map.of("error", "Failed to fetch teacher list: " + e.getMessage()));
        }
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateTeacher(@PathVariable Long id, @RequestBody Teacher teacher) {
        try {
            TeacherDto updatedTeacher = teacherService.updateTeacher(id, teacher);
            return ResponseEntity.ok(updatedTeacher);
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(Map.of("error", "Failed to update teacher: " + e.getMessage()));
        }
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTeacher(@PathVariable Long id) {
        try {
            String message = teacherService.deleteTeacher(id);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(Map.of("error", "Failed to delete teacher: " + e.getMessage()));
        }
    }
    // ✅ ASSIGN TEACHER TO CLASS & SECTION
    @PostMapping("/classes/{classId}/sections/{sectionId}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignTeacher(
            @PathVariable Long classId,
            @PathVariable Long sectionId,
            @RequestBody AssignTeacherRequest request
    ) {
        try {
            AssignedTeacherResponse response = teacherService.assignTeacher(classId, sectionId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(Map.of("error", "Failed to assign teacher: " + e.getMessage()));
        }
    }
    //view assigned teachers
    @GetMapping("/class/{classId}/teachers")
    public ResponseEntity<List<AssignedTeacherResponse>> getAssignedTeachers(@PathVariable Long classId) {
        return ResponseEntity.ok(teacherService.getAssignedTeachers(classId));
    }
}

package com.digital.controller;

import com.digital.dto.EnrollmentRequest;
import com.digital.dto.StudentCreateResponse;
import com.digital.dto.StudentRequest;
import com.digital.dto.StudentResponse;
import com.digital.servicei.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/students/admin")
@RequiredArgsConstructor
public class AdminStudentController {

    private final StudentService studentService;
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@RequestBody StudentRequest request) {
        StudentResponse response = studentService.createStudent(request);
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Long id,
                                                         @RequestBody StudentRequest request) {
        return ResponseEntity.ok(studentService.updateStudent(id, request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    @GetMapping(value = "/all")
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        String response = studentService.deleteStudent(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{studentId}/enroll")
    public ResponseEntity<StudentCreateResponse> enrollStudent(
            @PathVariable Long studentId,
            @RequestBody EnrollmentRequest request) {
        StudentCreateResponse response = studentService.enrollStudent(studentId, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<StudentResponse>> getStudentsByClass(
            @PathVariable Long classId,
            @RequestParam(value = "sectionId", required = false) Long sectionId) {
        List<StudentResponse> students = studentService.getStudentsByClass(classId, sectionId);
        return ResponseEntity.ok(students);
    }
}

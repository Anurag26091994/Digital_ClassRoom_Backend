package com.digital.controller;

import com.digital.dto.StudentProfileUpdateRequest;
import com.digital.dto.StudentResponse;
import com.digital.servicei.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentService studentService;

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/me")
    public ResponseEntity<StudentResponse> getMyProfile() {
        return ResponseEntity.ok(studentService.getMyProfile());
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PutMapping("/me")
    public ResponseEntity<StudentResponse> updateMyProfile(@RequestBody StudentProfileUpdateRequest request) {
        return ResponseEntity.ok(studentService.updateMyProfile(request));
    }
}

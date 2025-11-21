package com.digital.controller;

import com.digital.dto.AssignmentRequest;
import com.digital.dto.AssignmentResponse;
import com.digital.exception.ResourceNotFoundException;
import com.digital.exception.FileUploadException;
import com.digital.servicei.AssignmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/teacher/assignments")
@CrossOrigin("*")
public class TeacherAssignmentController {

    @Autowired
    private AssignmentService assignmentService;


    private Long getTeacherId() {
        return 1L;
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping(path = "/save", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createAssignment(
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("dueDate") String dueDateIso,
            @RequestParam(value = "createdAt", required = false) String createdAtIso,
            @RequestParam(value = "updatedAt", required = false) String updatedAtIso,
            @RequestParam("classId") Long classId,
            @RequestParam("sectionId") Long sectionId,
            @RequestParam("subjectId") Long subjectId,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            AssignmentRequest request = new AssignmentRequest();
            request.setTitle(title);
            request.setDescription(description);
            request.setDueDate(LocalDateTime.parse(dueDateIso));
            request.setClassId(classId);
            request.setSectionId(sectionId);
            request.setSubjectId(subjectId);

            if (createdAtIso != null && !createdAtIso.isEmpty()) {
                request.setCreatedAt(LocalDateTime.parse(createdAtIso));
            } else {
                request.setCreatedAt(LocalDateTime.now());
            }

            if (updatedAtIso != null && !updatedAtIso.isEmpty()) {
                request.setUpdatedAt(LocalDateTime.parse(updatedAtIso));
            } else {
                request.setUpdatedAt(request.getCreatedAt());
            }

            java.sql.Blob blob = assignmentService.convertMultipartToBlob(file);
            request.setFileUrl(blob);

            AssignmentResponse resp = assignmentService.createAssignment(request, getTeacherId());
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (FileUploadException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("File upload error: " + ex.getMessage());
        } catch (DateTimeParseException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid date format: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error: " + ex.getMessage());
        }
    }
    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping(path = "/{assignmentId}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateAssignment(
            @PathVariable Long assignmentId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "dueDate", required = false) String dueDateIso,
            @RequestParam(value = "updatedAt", required = false) String updatedAtIso,
            @RequestParam(value = "classId", required = false) Long classId,
            @RequestParam(value = "sectionId", required = false) Long sectionId,
            @RequestParam(value = "subjectId", required = false) Long subjectId,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            AssignmentRequest request = new AssignmentRequest();
            request.setTitle(title);
            request.setDescription(description);

            if (dueDateIso != null && !dueDateIso.isEmpty()) {
                request.setDueDate(LocalDateTime.parse(dueDateIso));
            }
            request.setClassId(classId);
            request.setSectionId(sectionId);
            request.setSubjectId(subjectId);

            if (updatedAtIso != null && !updatedAtIso.isEmpty()) {
                request.setUpdatedAt(LocalDateTime.parse(updatedAtIso));
            } else {
                request.setUpdatedAt(LocalDateTime.now());
            }

            if (file != null && !file.isEmpty()) {
                java.sql.Blob blob = assignmentService.convertMultipartToBlob(file);
                request.setFileUrl(blob);
            }

            AssignmentResponse resp = assignmentService.updateAssignment(assignmentId, request, getTeacherId());
            return ResponseEntity.ok(resp);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        } catch (FileUploadException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("File upload error: " + ex.getMessage());
        } catch (DateTimeParseException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid date format: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/{assignmentId}")
    public ResponseEntity<?> getAssignmentById(@PathVariable Long assignmentId) {
        try {
            AssignmentResponse assignment = assignmentService
                    .getAssignmentByIdAndTeacherId(assignmentId, getTeacherId());
            return ResponseEntity.ok(assignment);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping
    public ResponseEntity<List<AssignmentResponse>> getAllAssignments() {
        List<AssignmentResponse> assignments =
                assignmentService.getAllAssignmentsByTeacherId(getTeacherId());
        return ResponseEntity.ok(assignments);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<?> deleteAssignment(@PathVariable Long assignmentId) {
        try {
            assignmentService.deleteAssignmentByIdAndTeacherId(assignmentId, getTeacherId());
            return ResponseEntity.ok("Assignment deleted successfully.");
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error: " + ex.getMessage());
        }
    }
}

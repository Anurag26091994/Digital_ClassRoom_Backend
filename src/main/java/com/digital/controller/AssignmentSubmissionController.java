package com.digital.controller;

import com.digital.dto.SubmissionResponse;
import com.digital.exception.ResourceNotFoundException;
import com.digital.exception.SubmissionAlreadyExistsException;
import com.digital.exception.FileUploadException;
import com.digital.servicei.AssignmentSubmissionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/submissions")
public class AssignmentSubmissionController {

    @Autowired
    private AssignmentSubmissionService submissionService;

    public static class ErrorResponse {
        private LocalDateTime date;
        private String httpStatus;
        private int statusCode;
        private String message;
        private String uriPath;

        public ErrorResponse(LocalDateTime date, String httpStatus, int statusCode, String message, String uriPath) {
            this.date = date;
            this.httpStatus = httpStatus;
            this.statusCode = statusCode;
            this.message = message;
            this.uriPath = uriPath;
        }

        public LocalDateTime getDate() {
            return date;
        }

        public String getHttpStatus() {
            return httpStatus;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getMessage() {
            return message;
        }

        public String getUriPath() {
            return uriPath;
        }
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping
    public ResponseEntity<?> submitAssignment(
            @RequestParam("studentId") Long studentId,
            @RequestParam("assignmentId") Long assignmentId,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ) {
        try {
            SubmissionResponse response = submissionService.submitAssignment(studentId, assignmentId, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (SubmissionAlreadyExistsException | ResourceNotFoundException | FileUploadException e) {
            return buildErrorResponse(e, HttpStatus.BAD_REQUEST, request);
        } catch (Exception e) {
            return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    // Both STUDENT and ADMIN (or TEACHER) can fetch a submission
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{submissionId}")
    public ResponseEntity<?> getSubmission(@PathVariable Long submissionId, HttpServletRequest request) {
        try {
            SubmissionResponse response = submissionService.getSubmission(submissionId);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return buildErrorResponse(e, HttpStatus.NOT_FOUND, request);
        } catch (Exception e) {
            return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    // Student can update their submission (file), but not feedback/marks
    @PreAuthorize("hasRole('STUDENT')")
    @PutMapping("/{submissionId}/resubmit")
    public ResponseEntity<?> updateSubmissionFile(
            @PathVariable Long submissionId,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ) {
        try {
            SubmissionResponse response = submissionService.updateSubmission(submissionId, file);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return buildErrorResponse(e, HttpStatus.NOT_FOUND, request);
        } catch (FileUploadException e) {
            return buildErrorResponse(e, HttpStatus.BAD_REQUEST, request);
        } catch (Exception e) {
            return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    // Admin / Teacher updates feedback and marks
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @PutMapping("/{submissionId}/feedback")
    public ResponseEntity<?> updateFeedbackAndMarks(
            @PathVariable Long submissionId,
            @RequestParam("feedback") String feedback,
            @RequestParam("marks") Double marks,
            HttpServletRequest request
    ) {
        try {
            SubmissionResponse response = submissionService.updateFeedbackAndMarks(submissionId, feedback, marks);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return buildErrorResponse(e, HttpStatus.NOT_FOUND, request);
        } catch (Exception e) {
            return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @PreAuthorize("hasRole('STUDENT')")
    @DeleteMapping("/{submissionId}")
    public ResponseEntity<?> deleteSubmission(@PathVariable Long submissionId, HttpServletRequest request) {
        try {
            submissionService.deleteSubmission(submissionId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return buildErrorResponse(e, HttpStatus.NOT_FOUND, request);
        } catch (Exception e) {
            return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception ex, HttpStatus status, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.getReasonPhrase(),
                status.value(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }
}

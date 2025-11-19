package com.digital.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomResponse> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<CustomResponse> handleDuplicateResource(DuplicateResourceException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(CustomUnauthorizedException.class)
    public ResponseEntity<CustomResponse> handleUnauthorized(CustomUnauthorizedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(CustomForbiddenException.class)
    public ResponseEntity<CustomResponse> handleForbidden(CustomForbiddenException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({PaymentException.class, InvalidPaymentException.class})
    public ResponseEntity<CustomResponse> handlePaymentExceptions(RuntimeException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }



@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<Map<String, String>> handleValidationExceptions(
        MethodArgumentNotValidException ex) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
    );

    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
}
    @ExceptionHandler(ExamNotFoundException.class)
    public ResponseEntity<?> handleExamNotFound(ExamNotFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "Exam Not Found");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(DuplicateExamScheduleException.class)
    public ResponseEntity<?> handleDuplicateExam(DuplicateExamScheduleException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Duplicate Exam Schedule", ex.getMessage());
    }

    @ExceptionHandler(InvalidSubmissionException.class)
    public ResponseEntity<?> handleInvalidSubmission(InvalidSubmissionException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Submission", ex.getMessage());
    }

    @ExceptionHandler(ResultAlreadyPublishedException.class)
    public ResponseEntity<?> handleResultPublished(ResultAlreadyPublishedException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Result Already Published", ex.getMessage());
    }

    private ResponseEntity<?> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage() != null ? ex.getMessage() : "Internal server error",
                request.getRequestURI()
        );
    }

    private ResponseEntity<CustomResponse> buildResponse(HttpStatus status, String message, String path) {
        CustomResponse response = new CustomResponse(
                LocalDateTime.now(),
                status,
                status.value(),
                message,
                path
        );
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<CustomResponse> invalidDateException(InvalidDateException e, HttpServletRequest request){

        CustomResponse customResponse = new CustomResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_ACCEPTABLE,
                HttpStatus.NOT_ACCEPTABLE.value(),
                e.getMessage(),
                request.getRequestURI());

        return new ResponseEntity<>(customResponse, HttpStatus.NOT_ACCEPTABLE);
    }
    @ExceptionHandler(LeaveOverlappingException.class)
    public ResponseEntity<CustomResponse> leaveOverlappingException(LeaveOverlappingException e,
                                                                    HttpServletRequest request){
        CustomResponse customResponse = new CustomResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                request.getRequestURI());

        return new ResponseEntity<>(customResponse, HttpStatus.BAD_REQUEST);
    }
}

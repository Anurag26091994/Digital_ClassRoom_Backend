package com.digital.exception;

public class DuplicateExamScheduleException extends RuntimeException {
    public DuplicateExamScheduleException(String message) {
        super(message);
    }
}

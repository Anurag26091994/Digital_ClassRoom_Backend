package com.digital.exception;

public class SubmissionAlreadyExistsException extends RuntimeException {
    public SubmissionAlreadyExistsException(String message) {
        super(message);
    }
}
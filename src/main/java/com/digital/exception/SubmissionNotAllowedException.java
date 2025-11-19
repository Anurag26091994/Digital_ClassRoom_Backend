package com.digital.exception;

public class SubmissionNotAllowedException extends RuntimeException {
    public SubmissionNotAllowedException(String message) {
        super(message);
    }
}

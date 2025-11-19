package com.digital.exception;

public class ResultAlreadyPublishedException extends RuntimeException {
    public ResultAlreadyPublishedException(String message) {
        super(message);
    }
}

package com.digital.exception;

public class FileUploadException extends RuntimeException {
    public FileUploadException(String message, Exception e) {
        super(message);
    }
}

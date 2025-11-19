package com.digital.exception;

public class LibraryException extends RuntimeException {
    public LibraryException(String msg) {
        super(msg);
    }
    public LibraryException(String msg, Throwable cause) {
        super(msg, cause);
    }
}


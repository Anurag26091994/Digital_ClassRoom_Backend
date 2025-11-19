package com.digital.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class CustomErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
    private HttpStatus httpStatus;
    private Integer statusCode;
    private String customMessage;
    private String uriPath;

    public CustomErrorResponse(HttpStatus httpStatus, String customMessage, String uriPath) {
        this.date = LocalDateTime.now();
        this.httpStatus = httpStatus;
        this.statusCode = httpStatus.value();
        this.customMessage = customMessage;
        this.uriPath = uriPath;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getCustomMessage() {
        return customMessage;
    }

    public String getUriPath() {
        return uriPath;
    }
}

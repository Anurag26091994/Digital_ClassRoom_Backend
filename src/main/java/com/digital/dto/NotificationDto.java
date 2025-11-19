package com.digital.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationDto {
    private Long id;
    private String message;
    private String type;
    private String status;
    private LocalDateTime createdAt;
}

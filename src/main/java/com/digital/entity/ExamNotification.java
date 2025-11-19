package com.digital.entity;

import com.digital.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "exam_notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exam_notificationId;


    // the receiver user id (student or teacher user id)
    private Long userId;


    private String title;


    @Column(length = 2000)
    private String message;


    @Enumerated(EnumType.STRING)
    private NotificationType type;


    // optional reference to exam/result/report card
    private Long referenceId;


    private boolean isSeen = false;


    private LocalDateTime createdAt = LocalDateTime.now();
}




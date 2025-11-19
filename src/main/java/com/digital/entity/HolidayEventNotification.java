package com.digital.entity;

import com.digital.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class HolidayEventNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long holidayEventNotificationId;

    private Long userId;

    private String title;

    @Column(length = 2000)
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    // optional reference to exam/result/report card
    private Long referenceId;

    private boolean isSeen = false;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

package com.digital.entity;


import com.digital.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;


    @Entity
    @Table(name = "ptm_notification")
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public class PTMNotification {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "parent_id")
        private Parent parent;

        private String title;
        private String message;

        @Enumerated(EnumType.STRING)
        private NotificationType type; // e.g., PTM

        private LocalDateTime createdAt;
}

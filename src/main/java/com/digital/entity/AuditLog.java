package com.digital.entity;

import com.digital.enums.Action;
import com.digital.enums.Module;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long auditLogId;

    private Long userId;

    private String username;

    @Enumerated(EnumType.STRING)
    private Action action;

    @Enumerated(EnumType.STRING)
    private Module module;

    private LocalDateTime time;
}

package com.digital.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
@JsonIgnoreProperties({"schoolClass", "section", "teacher", "timetable"})
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long sessionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timetableId", nullable = false)
    private Timetable timetable;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "classId", nullable = false)
    private SchoolClass schoolClass;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sectionId", nullable = false)
    private Section section;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacherId", nullable = false)
    private Teacher teacher;

    @FutureOrPresent(message = "Only Present or Future date is allowed")
    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @NotBlank
    @Size(min = 2, max = 50, message = "Topic must be between 2 to 50 characters")
    @Column(nullable = false)
    private String topic;

    @Size(max = 300, message = "Only 300 characters are allowed in description")
    private String description;

    private String joinLink;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void setCreatedAt(){
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void setUpdated(){
        this.updatedAt = LocalDateTime.now();
    }
}

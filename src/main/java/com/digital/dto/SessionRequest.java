package com.digital.dto;

import com.digital.entity.SchoolClass;
import com.digital.entity.Section;
import com.digital.entity.Teacher;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class SessionRequest {

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

    @Future(message = "Only future time is allowed")
    @Column(nullable = false)
    private LocalDateTime startTime;

    @Future(message = "Only future time is allowed")
    @Column(nullable = false)
    private LocalDateTime endTime;

    @NotBlank
    @Size(min = 2, max = 50, message = "Topic must be between 2 to 50 characters")
    @Column(nullable = false)
    private String topic;

    @Size(max = 300, message = "Only 300 characters are allowed in description")
    private String description;
}

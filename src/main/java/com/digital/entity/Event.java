package com.digital.entity;

import com.digital.enums.EventType;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long eventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendarId", nullable = false)
    private AcademicCalendar calendar; //(FK → AcademicCalendar)

    @Enumerated(EnumType.STRING)
    private EventType eventType; // (ENUM: Holiday, Exam, ParentMeeting, SportsDay, Workshop, CustomEvent)

    @NotBlank(message = "Event name cannot be null")
    @Column(nullable = false)
    private String eventName; // (String)

    @FutureOrPresent(message = "Event date must be from present or future")
    @Column(nullable = false)
    private LocalDate eventDate; // (Date)

    @NotNull(message = "School classes cannot be null")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable (
            name = "event_schoolClass",
            joinColumns = @JoinColumn(name = "eventId"),
            inverseJoinColumns = @JoinColumn(name = "classId")
    )
    private List<SchoolClass> schoolClasses; // (FK → Class, Nullable → Some events may be class-specific)

    @NotNull(message = "Sections cannot be null")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "event_section",
            joinColumns = @JoinColumn(name = "eventId"),
            inverseJoinColumns = @JoinColumn(name = "sectionId")
    )
    private List<Section> sections; // (FK → Section, Nullable)

    @NotBlank(message = "Description cannot be null")
    @Column(nullable = false)
    private String description;
}

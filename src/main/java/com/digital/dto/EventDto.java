package com.digital.dto;

import com.digital.enums.EventType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class EventDto {

    private Long eventId;

    private Long calenderId;

    private String academicYear;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private String eventName;

    private LocalDate eventDate;

    private List<String> schoolClasses;

    private List<String> sections;

    private String description;
}

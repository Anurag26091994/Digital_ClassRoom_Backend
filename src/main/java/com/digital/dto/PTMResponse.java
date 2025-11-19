package com.digital.dto;

import com.digital.enums.PTMStatus;
import com.digital.enums.PTMType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PTMResponse {
//    private Long ptmId;
//    private String title;
//    private String description;
//    private LocalDateTime meetingDateTime;
//    private int durationMinutes;     // Added
//    private PTMType type;
//    private boolean online;          // Added
//    private String joinLink;
//    private String venue;
//    private PTMStatus status;
//    private List<Long> studentIds;   // Added
private Long ptmId;
    private String title;
    private String description;
    private LocalDateTime meetingDateTime;
    private PTMType type;
    private String joinLink;
    private String venue;
    private PTMStatus status;
    private Integer durationMinutes;
    private List<Long> studentIds;
}

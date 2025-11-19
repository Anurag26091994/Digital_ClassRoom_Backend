package com.digital.dto;

import com.digital.enums.PTMType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PTMRequest {
//    private String title;
//    private String description;
//    private LocalDateTime meetingDateTime;
//    private PTMType type;
//    private String joinLink;
//    private String venue;
//    private List<Long> studentIds;
    private Long ptmId;
private String title;
    private String description;
    private LocalDateTime meetingDateTime;
    private PTMType type;  // ONLINE / OFFLINE
    private Integer durationMinutes;
    private String venue;
    private List<Long> studentIds;
}

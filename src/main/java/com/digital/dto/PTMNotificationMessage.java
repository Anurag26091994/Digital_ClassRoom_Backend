package com.digital.dto;

import com.digital.enums.PTMType;
import lombok.*;


import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PTMNotificationMessage {
    private Long ptmId;
    private String title;
    private String message;
    private PTMType mode;
    private String joinLink;
    private String venue;
    private LocalDateTime meetingDate;
}

package com.digital.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {
    private Long reservationId;
    private Long bookId;
    private Long memberId;
    private String status;
    private LocalDateTime reservationDate;
}

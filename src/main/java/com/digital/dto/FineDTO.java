package com.digital.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FineDTO {
    private Long fineId;
    private Long issueId;
    private Long memberId;
    private Double fineAmount;
    private String fineReason;
    private String fineStatus;
    private LocalDate paidDate;
    private Long paymentId;


}


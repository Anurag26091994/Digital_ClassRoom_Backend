package com.digital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FineRequest {
    private Long issueId;
    private String reason;
    private Double overrideAmount;
}

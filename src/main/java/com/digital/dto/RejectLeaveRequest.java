package com.digital.dto;

import com.digital.enums.LeaveRequestStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class RejectLeaveRequest {

    @Enumerated(EnumType.STRING)
    private LeaveRequestStatus status;

    private String remarks;
}

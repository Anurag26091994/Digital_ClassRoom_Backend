package com.digital.dto;

import com.digital.enums.MemberStatus;
import com.digital.enums.MembershipType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class MemberCreateRequest {
    private Long userId;

    @Enumerated(EnumType.STRING)
    private MembershipType membershipType;



}



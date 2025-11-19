package com.digital.dto;

import com.digital.enums.MemberStatus;
import com.digital.enums.MembershipType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {
    private Long memberId;
    private Long userId;

    @Enumerated(EnumType.STRING)
    private MembershipType membershipType;

    private LocalDate joinDate;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private Integer totalIssuedBooks;


}
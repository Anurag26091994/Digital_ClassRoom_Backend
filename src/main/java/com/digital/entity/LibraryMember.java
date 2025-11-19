package com.digital.entity;

import com.digital.enums.MemberStatus;
import com.digital.enums.MembershipType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "library_members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibraryMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private MembershipType membershipType;

    private LocalDate joinDate;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private Integer totalIssuedBooks;


}

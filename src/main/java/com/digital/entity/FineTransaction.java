package com.digital.entity;

import com.digital.enums.FineStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fine_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FineTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fineId;

    @OneToOne
    @JoinColumn(name = "issue_id")
    private BookIssue issue;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private LibraryMember member;

    private Double fineAmount;

    private String fineReason;

    @Enumerated(EnumType.STRING)
    private FineStatus fineStatus;

    private LocalDate paidDate;

    private Long paymentId;  // reference to Payment module (nullable)


}

package com.digital.entity;

import com.digital.enums.IssueStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "book_issues")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookIssue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long issueId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private LibraryMember member;

    private LocalDate issueDate;
    private LocalDate dueDate;

    private LocalDate returnDate;

    private Double fineAmount = 0.0;

    @Enumerated(EnumType.STRING)
    private IssueStatus status;


}


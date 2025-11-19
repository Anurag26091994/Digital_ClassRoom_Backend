package com.digital.entity;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
@Entity
@Table(name = "book_reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private LibraryMember member;

    private LocalDateTime reservationDate;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    public enum ReservationStatus {
        ACTIVE,
        CANCELLED,
        COMPLETED
    }
}

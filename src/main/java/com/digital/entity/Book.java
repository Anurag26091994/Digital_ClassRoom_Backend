package com.digital.entity;

import com.digital.enums.BookStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    private String title;
    private String author;
    private String publisher;

    @Column(unique = true)
    private String isbn;

    private String category;
    private String edition;

    private Integer totalCopies;
    private Integer availableCopies;

    private String shelfNumber;

    private LocalDate addedDate;

    @Enumerated(EnumType.STRING)
    private BookStatus status;


}

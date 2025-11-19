package com.digital.repository;

import com.digital.entity.Book;
import com.digital.entity.BookReservation;
import com.digital.entity.LibraryMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookReservationRepository extends JpaRepository<BookReservation, Long> {

    List<BookReservation> findByMemberAndStatus(LibraryMember member, BookReservation.ReservationStatus status);

    List<BookReservation> findByMember(LibraryMember member);

    List<BookReservation> findByBookAndStatus(Book book, BookReservation.ReservationStatus status);

    boolean existsByBook(Book book);
}

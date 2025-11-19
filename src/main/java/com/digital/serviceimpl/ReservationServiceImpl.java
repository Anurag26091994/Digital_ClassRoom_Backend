package com.digital.serviceimpl;

import com.digital.dto.ReservationDTO;
import com.digital.entity.Book;
import com.digital.entity.BookReservation;
import com.digital.entity.LibraryMember;
import com.digital.enums.EventType;
import com.digital.exception.BusinessException;
import com.digital.exception.NotFoundException;
import com.digital.repository.BookRepository;
import com.digital.repository.BookReservationRepository;
import com.digital.repository.LibraryMemberRepository;
import com.digital.servicei.IssueService;
import com.digital.servicei.NotificationService;
import com.digital.servicei.ReservationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final BookReservationRepository reservationRepo;
    private final BookRepository bookRepo;
    private final LibraryMemberRepository memberRepo;
    private final IssueService issueService;
    private final NotificationService notificationService;

    @Override
    public ReservationDTO reserveBook(Long bookId, Long memberId) {
        System.out.println("reserveBook called with memberId = " + memberId + ", bookId = " + bookId);

        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found with id: " + bookId));

        LibraryMember member = memberRepo.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found with id: " + memberId));

        // Only allow reservation if book is fully unavailable (no copies)
        if (book.getAvailableCopies() != null && book.getAvailableCopies() > 0) {
            throw new BusinessException("Book is available now; reserve only when unavailable");
        }

        // Check if the member already has an active reservation for this book
        List<BookReservation> existing = reservationRepo.findByMemberAndStatus(member, BookReservation.ReservationStatus.ACTIVE);
        boolean already = existing.stream()
                .anyMatch(r -> r.getBook().getBookId().equals(bookId));
        if (already) {
            throw new BusinessException("You already have an active reservation for this book");
        }

        BookReservation res = BookReservation.builder()
                .book(book)
                .member(member)
                .reservationDate(LocalDateTime.now())
                .status(BookReservation.ReservationStatus.ACTIVE)
                .build();

        res = reservationRepo.save(res);

        // Send notification
        notificationService.sendNotification(
                member.getUserId(),
                EventType.RESERVATION_AVAILABLE,
                "Your reservation is active for book: " + book.getTitle()
        );

        return mapToDTO(res);
    }

    @Override
    @Transactional
    public ReservationDTO approveReservation(Long reservationId) {
        BookReservation res = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found with id: " + reservationId));

        if (res.getStatus() != BookReservation.ReservationStatus.ACTIVE) {
            throw new BusinessException("Reservation is not active; cannot approve");
        }

        Book book = res.getBook();

        // ✅ Explicit check for available copies BEFORE issuing
        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            throw new BusinessException("No available copies for book id " + book.getBookId());
        }

        // Proceed to issue the book
        issueService.issueBook(book.getBookId(), res.getMember().getMemberId());

        // Mark reservation as completed
        res.setStatus(BookReservation.ReservationStatus.COMPLETED);
        reservationRepo.save(res);

        // Notify the member
        notificationService.sendNotification(
                res.getMember().getUserId(),
                EventType.RESERVATION_AVAILABLE,
                "Your reserved book has been issued: " + book.getTitle()
        );

        return mapToDTO(res);
    }

    @Override
    @Transactional
    public ReservationDTO cancelReservation(Long reservationId) {
        BookReservation res = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found with id: " + reservationId));

        if (res.getStatus() != BookReservation.ReservationStatus.ACTIVE) {
            throw new BusinessException("Only active reservations can be cancelled");
        }

        res.setStatus(BookReservation.ReservationStatus.CANCELLED);
        reservationRepo.save(res);

        notificationService.sendNotification(
                res.getMember().getUserId(),
                EventType.RESERVATION_AVAILABLE,
                "Your reservation was cancelled for book: " + res.getBook().getTitle()
        );

        return mapToDTO(res);
    }

    @Override
    public List<ReservationDTO> listReservationsByMember(Long memberId) {
        LibraryMember member = memberRepo.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found with id: " + memberId));

        List<BookReservation> list = reservationRepo.findByMemberAndStatus(member, BookReservation.ReservationStatus.ACTIVE);
        return list.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDTO> listActiveReservationsForBook(Long bookId) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found with id: " + bookId));

        List<BookReservation> list = reservationRepo.findByBookAndStatus(book, BookReservation.ReservationStatus.ACTIVE);
        return list.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ReservationDTO mapToDTO(BookReservation res) {
        return ReservationDTO.builder()
                .reservationId(res.getReservationId())
                .bookId(res.getBook().getBookId())
                .memberId(res.getMember().getMemberId())
                .reservationDate(res.getReservationDate())
                .status(res.getStatus().name())
                .build();
    }
}

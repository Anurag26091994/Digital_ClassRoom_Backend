package com.digital.serviceimpl;

import com.digital.dto.IssueDTO;
import com.digital.entity.Book;
import com.digital.entity.BookIssue;
import com.digital.entity.BookReservation;
import com.digital.entity.LibraryMember;
import com.digital.entity.FineTransaction;
import com.digital.enums.IssueStatus;

import com.digital.enums.MemberStatus;
import com.digital.enums.FineStatus;
import com.digital.enums.EventType;
import com.digital.exception.BusinessException;
import com.digital.exception.NotFoundException;
import com.digital.repository.BookRepository;
import com.digital.repository.LibraryMemberRepository;
import com.digital.repository.BookIssueRepository;
import com.digital.repository.BookReservationRepository;
import com.digital.repository.FineTransactionRepository;
import com.digital.servicei.IssueService;
import com.digital.servicei.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {

    private final BookRepository bookRepo;
    private final LibraryMemberRepository memberRepo;
    private final BookIssueRepository issueRepo;
    private final BookReservationRepository reservationRepo;
    private final FineTransactionRepository fineRepo;

    private final NotificationService notificationService;

    private static final int MAX_ISSUE = 3;
    private static final double FINE_PER_DAY = 5.0;

    @Override
    @Transactional
    public IssueDTO issueBook(Long bookId, Long memberId) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found with id " + bookId));

        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            throw new BusinessException("No available copies for book id " + bookId);
        }

        LibraryMember mem = memberRepo.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found with id " + memberId));

        if (!MemberStatus.ACTIVE.equals(mem.getStatus())) {
            throw new BusinessException("Member is inactive, cannot issue books");
        }

        Integer totalIssued = mem.getTotalIssuedBooks() != null ? mem.getTotalIssuedBooks() : 0;
        if (totalIssued >= MAX_ISSUE) {
            throw new BusinessException("Member has already reached maximum issue limit");
        }

        // Check reservations for this book
        List<BookReservation> activeRes = reservationRepo.findByBookAndStatus(book, BookReservation.ReservationStatus.ACTIVE);
        if (!activeRes.isEmpty()) {
            boolean reservedByOthers = activeRes.stream()
                    .anyMatch(r -> !r.getMember().getMemberId().equals(memberId));
            if (reservedByOthers) {
                throw new BusinessException("Cannot issue: book is reserved by another member");
            }
        }

        LocalDate today = LocalDate.now();

//        LocalDate due = today.plusDays(5);
        LocalDate due = today.minusDays(3);

        BookIssue issue = BookIssue.builder()
                .book(book)
                .member(mem)
                .issueDate(today)
                .dueDate(due)
                .status(IssueStatus.ISSUED)
//                .status(BookIssue.IssueStatus.ISSUED)
                .fineAmount(0.0)
                .build();
        issue = issueRepo.save(issue);

        // Update book
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        if (book.getAvailableCopies() <= 0) {
            book.setAvailableCopies(0);

        }
        bookRepo.save(book);
        mem.setTotalIssuedBooks(totalIssued + 1);
        memberRepo.save(mem);


        notificationService.sendNotification(
                mem.getUserId(),
                EventType.BOOK_DUE_REMINDER,
                "Book issued: " + book.getTitle() + ". Due date: " + due
        );

        return mapToDTO(issue);
    }

    @Override
    @Transactional
    public IssueDTO returnBook(Long issueId) {
        BookIssue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new NotFoundException("Issue record not found with id " + issueId));
        FineTransaction savedfinetransaction = null;
//        BookIssue.IssueStatus current = issue.getStatus();
        IssueStatus current = issue.getStatus();
        if (!(IssueStatus.ISSUED.equals(current) || IssueStatus.OVERDUE.equals(current))) {
            throw new BusinessException("Cannot return book for issue with status: " + current);
        }
        LocalDate today = LocalDate.now();
        issue.setReturnDate(today);

        if (today.isAfter(issue.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(issue.getDueDate(), today);
            double fine = daysLate * FINE_PER_DAY;
            issue.setFineAmount(fine);
            issue.setStatus(IssueStatus.OVERDUE);
//            issue.setStatus(BookIssue.IssueStatus.ISSUED);


            // Record fine transaction
//            FineTransaction fineTransaction =fineRepo.findByIssue_IssueId(issueId);

            FineTransaction f = FineTransaction.builder()
                    .issue(issue)
                    .member(issue.getMember())
                    .fineAmount(fine)
                    .fineReason("Late return")
                    .fineStatus(FineStatus.UNPAID)

                    .build();
            savedfinetransaction = fineRepo.save(f);

            notificationService.sendNotification(
                    issue.getMember().getUserId(),
                    EventType.BOOK_OVERDUE,
                    "Book overdue: " + issue.getBook().getTitle() + ". Fine: ₹" + fine
            );
        }  else {
            issue.setFineAmount(0.0);
        }

        issue.setStatus(IssueStatus.ISSUED);
//        issue.setStatus(BookIssue.IssueStatus.RETURNED);
        issue = issueRepo.save(issue);

        // ✅ Update book copy count
        Book book = issue.getBook();
        book.setAvailableCopies((book.getAvailableCopies() == null ? 0 : book.getAvailableCopies()) + 1);
        bookRepo.save(book);

        // ✅ Update member’s issued count
        LibraryMember mem = issue.getMember();
        int issuedCount = mem.getTotalIssuedBooks() != null ? mem.getTotalIssuedBooks() : 0;
        mem.setTotalIssuedBooks(Math.max(0, issuedCount - 1));
        memberRepo.save(mem);

        IssueDTO issueDTO = mapToDTO(issue);
        if (savedfinetransaction == null) {
            issueDTO.setFineId(null);
        } else {
            issueDTO.setFineId(savedfinetransaction.getFineId());
        }
        return issueDTO;

    }

    @Override
    @Transactional
    public IssueDTO renewBook(Long issueId) {
        BookIssue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new NotFoundException("Issue record not found id " + issueId));

        if (!IssueStatus.ISSUED.equals(issue.getStatus())) {
            throw new BusinessException("Cannot renew unless issue status is ISSUED");
        }

        Book book = issue.getBook();
        List<BookReservation> resList = reservationRepo.findByBookAndStatus(book, BookReservation.ReservationStatus.ACTIVE);
        if (!resList.isEmpty()) {
            throw new BusinessException("Cannot renew: book is reserved by another member");
        }

        LocalDate newDue = issue.getDueDate().plusDays(14);
        issue.setDueDate(newDue);
        issue = issueRepo.save(issue);

        notificationService.sendNotification(
                issue.getMember().getUserId(),
                EventType.BOOK_DUE_REMINDER,
                "Book renewed: " + issue.getBook().getTitle() + ". New due date: " + newDue
        );

        return mapToDTO(issue);
    }

    @Override
    public List<IssueDTO> listIssuesByMember(Long memberId) {
        LibraryMember mem = memberRepo.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found with id " + memberId));

        List<BookIssue> list = issueRepo.findByMemberAndStatus(mem, IssueStatus.ISSUED);
        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<IssueDTO> listOverdueIssues() {
        List<BookIssue> all = issueRepo.findAll();
        return all.stream()
                .filter(i -> IssueStatus.OVERDUE.equals(i.getStatus()))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private IssueDTO mapToDTO(BookIssue issue) {
        return IssueDTO.builder()
                .issueId(issue.getIssueId())
                .bookId(issue.getBook().getBookId())
                .memberId(issue.getMember().getMemberId())
                .issueDate(issue.getIssueDate())
                .dueDate(issue.getDueDate())
                .returnDate(issue.getReturnDate())
                .fineAmount(issue.getFineAmount())
                .status(issue.getStatus().name())
//                .fineId(issue.getBook().)
                .build();
    }
}

package com.digital.serviceimpl;


import com.digital.dto.BookDTO;
import com.digital.entity.Book;
import com.digital.enums.BookStatus;
import com.digital.enums.IssueStatus;
import com.digital.exception.BusinessException;
import com.digital.exception.NotFoundException;
import com.digital.repository.BookIssueRepository;
import com.digital.repository.BookRepository;
import com.digital.repository.BookReservationRepository;
import com.digital.servicei.BookService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepo;
    private final BookIssueRepository issueRepository;
    private final BookReservationRepository reservationRepository;


    @Override
    @Transactional
    public BookDTO createBook(BookDTO dto) {
        if (dto.getIsbn() != null) {
            bookRepo.findByIsbn(dto.getIsbn()).ifPresent(b ->
                    { throw new BusinessException("ISBN already exists: " + dto.getIsbn()); }
            );
        }
        Book book = Book.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .publisher(dto.getPublisher())
                .isbn(dto.getIsbn())
                .category(dto.getCategory())
                .edition(dto.getEdition())
                .totalCopies(dto.getTotalCopies())
                .availableCopies(dto.getTotalCopies())
                .shelfNumber(dto.getShelfNumber())
                .addedDate(dto.getAddedDate() != null ? dto.getAddedDate() : LocalDate.now())
                .status(BookStatus.AVAILABLE)
                .build();
        book = bookRepo.save(book);
        dto.setBookId(book.getBookId());
        dto.setAvailableCopies(book.getAvailableCopies());
        dto.setAddedDate(book.getAddedDate());
        return dto;
    }

    @Override
    @Transactional
    public BookDTO updateBook(Long bookId, BookDTO dto) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found with id " + bookId));
        // update allowed fields
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setPublisher(dto.getPublisher());
        book.setCategory(dto.getCategory());
        book.setEdition(dto.getEdition());
        book.setShelfNumber(dto.getShelfNumber());

        // handle copy count adjustment
        if (dto.getTotalCopies() != null) {
            int diff = dto.getTotalCopies() - book.getTotalCopies();
            book.setTotalCopies(dto.getTotalCopies());
            book.setAvailableCopies(book.getAvailableCopies() + diff);
        }
        if (dto.getStatus() != null) {
            book.setStatus(BookStatus.valueOf(dto.getStatus()));
        }
        book = bookRepo.save(book);

        // map back to DTO
        BookDTO out = BookDTO.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .isbn(book.getIsbn())
                .category(book.getCategory())
                .edition(book.getEdition())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .shelfNumber(book.getShelfNumber())
                .addedDate(book.getAddedDate())
                .status(book.getStatus().name())
                .build();
        return out;
    }

    @Override
    @Transactional
    public void deleteBook(Long bookId) {
        Book book = bookRepo.findById(bookId)

                .orElseThrow(() -> new NotFoundException("Book not found with id " + bookId));
//      issueRepository.countByBookAndStatus(book, IssueStatus.ISSUED);
        long issuedCount = 0;
        issuedCount=  issueRepository.countByBookAndStatus(book, IssueStatus.ISSUED);
        // you could use custom query to count active issues
        // e.g. bookIssueRepository.countByBookAndStatus(...)
        if (issuedCount > 0) {
            throw new BusinessException("Cannot delete book; it is currently issued");
        }
        if (reservationRepository.existsByBook(book)) {
            throw new BusinessException("Cannot delete book; it is currently reserve");
        }
        bookRepo.delete(book);

    }

    @Override
    public BookDTO getBookById(Long bookId) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found with id " + bookId));
        return BookDTO.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .isbn(book.getIsbn())
                .category(book.getCategory())
                .edition(book.getEdition())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .shelfNumber(book.getShelfNumber())
                .addedDate(book.getAddedDate())
                .status(book.getStatus().name())
                .build();
    }

    @Override
    public List<BookDTO> searchBooks(String title, String author, String category) {
        // Simple example: search by title only if provided
        List<Book> books;
        if (title != null && !title.isBlank()) {
            books = bookRepo.findByTitleContainingIgnoreCase(title);
        } else {
            books = bookRepo.findAll();
        }
        // optionally filter by author / category
        return books.stream().map(book -> BookDTO.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .isbn(book.getIsbn())
                .category(book.getCategory())
                .edition(book.getEdition())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .shelfNumber(book.getShelfNumber())
                .addedDate(book.getAddedDate())
                .status(book.getStatus().name())
                .build()
        ).collect(Collectors.toList());
    }
}

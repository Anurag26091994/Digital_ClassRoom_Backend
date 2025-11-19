package com.digital.servicei;

import com.digital.dto.BookDTO;

import java.util.List;





public interface BookService {
    BookDTO createBook(BookDTO dto);
    BookDTO updateBook(Long bookId, BookDTO dto);
    void deleteBook(Long bookId);
    BookDTO getBookById(Long bookId);
    List<BookDTO> searchBooks(String title, String author, String category);
}
